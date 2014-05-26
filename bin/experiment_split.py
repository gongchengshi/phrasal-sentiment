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
    for i in range(4, len(feature_list)):
        for fl in combinations(feature_list, i):
            f = {}
            for feature in fl:
                f[feature] = feature_token_map[feature]
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

def filter_data(class_list, basepath, output_file, base):
    filepath = os.path.join(basepath, output_file)
    f = open(filepath, "w")
    min_num = len(base[class_list[0]])
    for class_string in class_list:
        l = len(base[class_string])
        min_num = min(min_num, l)

    data_filter = defaultdict(list)
    for class_string in base.iterkeys():
        for datum in random.sample(base[class_string], min(min_num, len(base[class_string]))):
            data_filter[class_string].append(datum)
            data_list = [ datum['ID'], datum['sentiment'] ] + \
                [ "%s=%s" % (key, value) for key,value in datum.iteritems() if key not in ["ID", "sentiment"]]
            f.write( " ".join(data_list) + "\n")
    return data_filter

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

    data_working = filter_data(["positive", "neutral", "negative"], basepath, "data_working.txt", data_base)

    fs = enumerate_feature_set(tfm, token_set)

    # for key in data_working.iterkeys():
    #     print key, len(data_working[key])
    # print len(fs)


if __name__ == '__main__':
    main(sys.argv[1:])
