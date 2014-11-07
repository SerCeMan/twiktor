#!/usr/bin/env python3

import random
import re
import pymorphy2
    
def setForm(wordToChange, wordWithForm):
    morph = pymorphy2.MorphAnalyzer()
    for form in morph.parse(wordToChange)[0].lexeme:
        if form.tag == morph.parse(wordWithForm)[0].tag:
            return form.word
    return wordWithForm
            
def generateSentence(sentence, dictionary):
    morph = pymorphy2.MorphAnalyzer()
    #Зачем ждать? Задайте нам вопрос прямо сейчас. Мы отвечаем круглосуточно на 10 языках
    sentence = sentence.replace(',', '')
    sentence = sentence.replace('.', '')
    sentence = sentence.replace('?', '')
    sentence = sentence.replace('!', '')
    
    words = sentence.rstrip().split(" ")
    answer = ""
    for word in words:
        if "PREP" in morph.parse(word)[0].tag:
            answer += word + " "
            continue
        
        normal_form = morph.parse(word)[0].normal_form
        synonym = ""
        if normal_form in dictionary:
            index = random.randint(0, len(dictionary[normal_form]) - 1)
            synonym = dictionary[normal_form][index]
        else:
            synonym = word
        if "ADVB" in morph.parse(word)[0].tag:
            answer += synonym + " "
        else:
            answer += setForm(synonym, word) + " "
    return answer[:-1]

dictionary = {}
lines = open("Synonym.txt").readlines()
for line in lines:
    words = line.rstrip().split(" ")
    dictionary[words[0]] = words[1:]

sentence = input().rstrip()
#sentence = "Я никогда еще не пробовал такого вкусного варенья"
newSentence = generateSentence(sentence, dictionary)
print(newSentence)

#morph = pymorphy2.MorphAnalyzer()
#print(morph.parse("на")[0].tag)
