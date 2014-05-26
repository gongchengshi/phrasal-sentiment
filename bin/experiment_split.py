#!/usr/bin/env python2.7

import sys
import argparse
import os
import random

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

def enumerate_feature_set(tfm, keep_token_list=None):
    feature_token_map = defaultdict(lambda: defaultdict( lambda: defaultdict(bool)))
    # print keep_token_list
    for key, value in tfm.iteritems():
        if keep_token_list is not None and key not in keep_token_list:
            continue
        feature_token_map[value][key] = True

    feature_list = [key for key in feature_token_map.iterkeys()]
    # print feature_list
    fs = []
    for i in range(6, len(feature_list)):
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

def sample_data(class_list, base):
    min_num = len(base[class_list[0]])
    for class_string in class_list:
        l = len(base[class_string])
        min_num = min(min_num, l)

    data_filter = defaultdict(list)
    for class_string in base.iterkeys():
        for datum in random.sample(base[class_string], min(min_num, len(base[class_string]))):
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
    for class_string in data.iterkeys():
        for datum in data[class_string]:
            f.write(datum_line(datum))
    f.close()

def datum_line(datum):
    data_list = [ datum['ID'], datum['sentiment'] ] + \
        [ "%s=%s" % (key, value) for key,value in datum.iteritems() if key not in ["ID", "sentiment"]]
    return " ".join(data_list) + "\n"

def output_experiments(data, token_dict, basepath):
    i = 0
    file_prefix = os.path.join(basepath, "data_exp_%d.txt")
    for exp in token_dict:
        i += 1
        filepath = file_prefix % i
        data_filter = filter_data(data, exp)
        output_data(data_filter, filepath, head="# " + " ".join([token for token in exp.iterkeys()]))

def main(arg=sys.argv[1:]):
    tfm = token_feature_map()

    parser = argparse.ArgumentParser()
    parser.add_argument('--project_output', help='location of the data.txt file', default='./')
    parser.add_argument('--random_sampling', help='perform random sampling of positive,neutral,negative with the least number of row from the three class',
        default=False, action='store_true')

    ns = parser.parse_args(arg)
    #print ns
    basepath = os.path.normpath(os.path.abspath(ns.project_output))
    (data_base, token_set) = parse_data(basepath, "data.txt")

    data_working = sample_data(["positive", "neutral", "negative"], data_base)

    output_data(data_working, os.path.join(basepath, "data_working.txt"))

    token_dict = enumerate_feature_set(tfm, token_set)

    print len(token_dict)
    output_experiments(data_working, token_dict, basepath)
    # for key in data_working.iterkeys():
    #     print key, len(data_working[key])


if __name__ == '__main__':
    main(sys.argv[1:])
