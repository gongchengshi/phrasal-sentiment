#!/usr/bin/env python2.7

import sys
import argparse
import os
import random
import stat

from collections import defaultdict
from itertools import *


def token_feature_map():
    return {
        'token' : 'phrasalVerbToken',
        'POS' : 'phrasalVerbPOS',
        'prevWord' : 'phrasalVerbContext',
        'nextWord' : 'phrasalVerbContext',
        'prev2Word' : 'phrasalVerbContext',
        'next2Word' : 'phrasalVerbContext',
        'prevPOS' : 'sentencePOSContext',
        'nextPOS' : 'sentencePOSContext',
        'adjectiveCount' : 'sentenceAdjectCount',
        'adverbCount' : 'sentenceAdverbCount',
        'pronounInSentence' : 'sentenceHasPronoun',
        'modalInSentence' : 'sentenceHasModal',
        'strongsubjCount' : 'sentenceWeakCount',
        'strongSubjRelation' : 'subjectiveModifierCount',
        'weakSubjRelation' : 'subjectiveModifierCount',
        'containsIntensifier' : 'containsIntensifier',
        'priorPolarity' : 'priorPolarity',
        'strongsubjCount' : 'sentenceStrongCount',
        'weaksubjCount' : 'sentenceWeakCount',
        'priorPolarityDefaultNeutral' : 'priorPolarityDefaultNeutral',
    }

def enumerate_feature_set(tfm, keep_token_list=None, features=6):
    feature_token_map = defaultdict(lambda: defaultdict( lambda: defaultdict(bool)))
    # print keep_token_list
    for key, value in tfm.iteritems():
        if keep_token_list is not None and key not in keep_token_list:
            continue
        feature_token_map[value][key] = True

    feature_list = [key for key in feature_token_map.iterkeys()]
    # print feature_list
    fs = []
    for i in range(min(features, len(feature_list)-1), len(feature_list)):
        for fl in combinations(feature_list, i):
            f = {}
            for feature in fl:
                for value in feature_token_map[feature].iterkeys():
                    f[value] = True
            fs.append(f)
    return fs

def parse_data(basepath, data_file):
    filepath = os.path.join(basepath, data_file)
    f = open(filepath, "r")
    data = defaultdict(list)
    token_set = set([])
    for line in f.readlines():
        line = line.strip()
        tokens = line.split(r" ")
        phrase_id = tokens[0]
        classify = tokens[1]
        d = {'ID' : phrase_id, 'sentiment' : classify}
        for token in tokens[2:]:
            if len(token) == 0: continue
            (key, value) = token.split("=")
            d[key] = value
            token_set.add(key)
        data[classify].append(d)
    return (data, token_set)

def sample_data(class_list, base, tokens):
    min_num = len(base[class_list[0]])
    for class_string in class_list:
        l = len(base[class_string])
        min_num = min(min_num, l)

    data_filter = defaultdict(list)
    data_filtered = filter_data(base, tokens)
    for class_string in data_filtered.iterkeys():
        for datum in random.sample(data_filtered[class_string], min(min_num, len(base[class_string]))):
            data_filter[class_string].append(datum)
    return data_filter

def filter_data(base, tokens):
    data = defaultdict(list)
    for class_string in base.iterkeys():
        for datum in base[class_string]:
            filter_datum = {}
            for key in tokens:
                if key in datum:
                    filter_datum[key] = datum[key]
            filter_datum['ID'] = datum['ID']
            filter_datum['sentiment'] = datum['sentiment']
            data[class_string].append(filter_datum)
    return data

def output_data(data, filepath, head=None):
    f = open(filepath, "w")
    if head:
        f.write(head + "\n")
    lines = []
    for class_string in data.iterkeys():
        for datum in data[class_string]:
            lines.append(datum_line(datum))
    for line in random.sample(lines, len(lines)):
        f.write(line)
    f.close()

def datum_line(datum):
    data_list = [ datum['ID'], datum['sentiment'] ] + \
        [ "%s=%s" % (key, value) for key,value in datum.iteritems() if key not in ["ID", "sentiment"]]
    return " ".join(data_list) + "\n"

def generate_run_script(basepath, file_prefix, num):
    filepath = os.path.join(basepath, "run_exp.sh")
    f = open(filepath, "w")
    f.write("#!/bin/sh\n")
    for i in range(1, num+1):
        current_file = file_prefix % i
        f.write("""
csv2vectors --input %s.txt --output %s.vector
vectors2classify --input %s.vector --training-portion 0.6 --num-trials 3 --trainer MaxEnt > %s.out  2> %s.err
""" % (current_file, current_file, current_file, current_file, current_file))
    f.close()
    st = os.stat(filepath)
    os.chmod(filepath, st.st_mode | stat.S_IEXEC)

def output_experiments(data, token_dict_list, basepath, experiments=60, generate_run=False):
    i = 0

    file_prefix = os.path.join(basepath, "data_exp_%d")
    for exp in random.sample(token_dict_list, min(experiments, len(token_dict_list))):
        i += 1
        filepath = (file_prefix % i) + ".txt"
        data_filter = filter_data(data, exp)
        output_data(data_filter, filepath, head="# " + " ".join([token for token in exp.iterkeys()]))
    if generate_run:
        generate_run_script(basepath, file_prefix, i)

def main(arg=sys.argv[1:]):
    tfm = token_feature_map()

    parser = argparse.ArgumentParser()
    parser.add_argument('--project_output', help='location of the data.txt file', default='./')
    parser.add_argument('--experiments', help="number of experiments to perform", default=60,
        type=int)
    parser.add_argument('--features', help="number of experiments to perform", default=6,
        type=int)
    parser.add_argument('--generate_run', help='generate experiement run file', action='store_true', default=False)
    parser.add_argument('--wanted_features', help="wanted features in csv format", type=str)

    ns = parser.parse_args(arg)

    basepath = os.path.normpath(os.path.abspath(ns.project_output))
    (data_base, token_set) = parse_data(basepath, "data.txt")

    base_tokens = [key for key in tfm.iterkeys()]
    if ns.wanted_features is not None:
        features = ns.wanted_features.split(",")
        feature_tokens = defaultdict(list)
        for token, feature in tfm.iteritems():
            feature_tokens[feature].append(token)
        base_tokens = []
        for feature in features:
            if feature in feature_tokens:
                base_tokens += feature_tokens[feature]
            else:
                raise Exception("invalid feature [%s]" % (feature))

    data_working = sample_data(["positive", "neutral", "negative"], data_base, base_tokens)

    output_data(data_working, os.path.join(basepath, "data_working.txt"))

    token_dict = enumerate_feature_set(tfm, token_set, features=ns.features)
    if ns.generate_run:
        output_experiments(data_working, token_dict, basepath, experiments=ns.experiments, generate_run=ns.generate_run)
    # for key in data_working.iterkeys():
    #     print key, len(data_working[key])


if __name__ == '__main__':
    main(sys.argv[1:])
