The creation and selection of features is a crucial part in the development of NER systems. The creation
of all included features will be presented in feature groups, with explanation of single features if this can be switched off automatically.
The description on how to switch on and off single features can be found in [User Guide](https://github.com/tudarmstadt-lt/GermaNER/blob/master/germaner/src/main/java/de/tu/darmstadt/lt/ner/doc/User-Guide.md).

## Character and Word Features
### N-gram Features
This feature group consists of the first and last character uni-, bi- and trigrams of the current token,
i.e.. prefixes and suffixes, time-shifted from -2 to +2. 

### Character Category Pattern
Similarly, character category pattern features, which are extracted from the current token based on unicode categories from clearTK (Bethard et al., 2014) are used, and were found to be an influential feature for the system. 

### Word Feature
Further, we use the words themselves as features in a window between -2 and 2

## NE Gazetteers
This gazetteer feature was created through the assembling of several lists containing NEs. Gazetteers may help to identify NEs that are known to be proper nouns in other contexts. 

###FreebaseList
For the FreebaseList, several Freebase lists containing proper nouns were merged. Freebase (Bollacker et al., 2008) is an English community-curated data-base containing well-known places, people and things under CC-BY-license. It contains 47 million lists. The largest task relevant lists as well as lists with frequent NEs such as Country or Currency were chosen for the final list. Please consult the paper for details of the incorporated lists, the preparation of the chosen content and the formatting of the list.

###Other Gazetteers
Several other gazetteers were incorporated as features, such as personal name lists extracted with the NameRec tool from ASV Toolbox (Biemann et al., 2008) from large, publicly available corpora. 

## Parts of Speech
### Clark's Unsupervised POS-tagger
We have incorporated automatically induced POS tags as POS features. This POS induction is based on the system by
Clark (2003), which clusters words into different classes in an unsupervised fashion, based on distributional
and morphological information. For this setup, we have used 10 million sentences, which are part of the Leipzig Corpora Collection (Richter et al. 2006), and induced 256 different classes.

### Mate POS tagger
Additionally, we experimented with classical POS features using the Mate POS tagger (Bohnet, 2010). Our tool will not include this feature as the licenses of the POS tagger and its training data would render our tool unusable for commercial purposes. However, we will provide the possibility to add this feature so that it can be used in an
academic setting.

## Word Similarity
This feature group consists of the four most similar words of the current token, obtained from the JoBimText4 (Biemann and Riedl, 2013) distributional thesaurus database, made available in a window of size 2.

## Topic Clusters
We have applied LDA topic modelling to the JoBimText German distributional thesaurus, using the thesaurus entries as ’documents’ for LDA. This results in a fixed number of topic clusters, most of which are quite pure in terms of syntactic and semantic class. We have generated different sets of such clusters, each for all words and for uppercase words only, and use the number of its most probable topic as a token’s feature – again, time-shifted in a range of -2 to 2. We experimented with sets of 50, 100, 200 and 500 clusters. In the final version we solely use the set of 200 clusters.

## Position feature
Position feature is the position of the token in the sentence.

## Case feature 
Case feature is the case of the token, distinguishing between uppercase and lowercase, the beginning of a sentence, camelCase and all uppercase, time shifted between -2 and 2.

