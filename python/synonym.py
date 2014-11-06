#!/usr/bin/env python3

import pymorphy2
morph = pymorphy2.MorphAnalyzer()
    
def setForm(wordToChange, wordWithForm):
    for form in morph.parse(wordToChange)[0].lexeme:
        if form.tag == morph.parse(wordWithForm)[0].tag:
            return form.word
            
def generateSentence(sentence, dictionary):
    words = sentence.rstrip().split(" ")
    answer = ""
    for word in words:
        """
        if (not "NOUN" in morph.parse(word)[0].tag) and (not "VERB" in morph.parse(word)[0].tag) and (not "ADJF" in morph.parse(word)[0].tag):
            answer += word + " "
            continue
        """
        normal_form = morph.parse(word)[0].normal_form
        synonym = ""
        if normal_form in dictionary:
            synonym = dictionary[normal_form][0]
        else:
            synonym = word
        newWord = setForm(synonym, word)
        """
        if (newWord is None) or (len(newWord) == 0):
            answer += word + " "
        else:
            answer += newWord + " "
        """
        print(newWord)
    return answer

dictionary = {}
lines = open("Synonym.txt").readlines()
for line in lines:
    words = line.rstrip().split(" ")
    dictionary[words[0]] = words[1:]

#sentence = input().rstrip()
sentence = "Я никогда еще не пробовал такого вкусного варенья"
newSentence = generateSentence(sentence, dictionary)
print(newSentence)
