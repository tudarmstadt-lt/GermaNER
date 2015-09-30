The [configuration file](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/config.properties) is used to configure different settings and feature options. 

Below are most important configuration elements.

_**Note**_:The you should use the same feature settings (see below) as the training one when you tag texts. The default model contains all of the following features, meaning that all features are used for training.

Setting a feature to =0 means turning it off, whereas setting it to =1 means turning it on. 

* [useFreeBase](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#freebaselist)=1 (if you use the less memory-expensive version, it is useFreeBase=0)
* [usePosition](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#position-feature)=1
* [useClarkPosInduction](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#clarks-unsupervised-pos-tagger)=1
* [useWordFeature](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-feature)=1
* [useCapitalFeature](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#case-feature)=1
* [usePreffix1Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1 (here the first character of the current, the previous and the following word are used)
* [usePreffix2Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1
(here the first two characters of the current, the previous and the following word are used)
* [usePreffix3Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1
(here the first three characters of the current, the previous and the following word are used)
* [usePreffix4Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1
(here the first four characters of the current, the previous and the following word are used)
* [useSuffix1Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1 (here the last character of the current, the previous and the following word are used)
* [useSuffix2Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1 (here the last two characters of the current, the previous and the following word are used)
* [useSuffix3Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1 (here the last three characters of the current, the previous and the following word are used)
useSuffix4Feature (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#n-gram-features)=1 (here the last four characters of the current, the previous and the following word are used)
* [useFirstNameFeature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#other-gazetteers)=1 
* [useSimilarWord1Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-similarity)=1 (here the most similar word is used)
* [useSimilarWord2Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-similarity)=1 (here the two most similar words are used)
* [useSimilarWord3Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-similarity)=1 (here the three most similar words are used)
* [useSimilarWord4Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#word-similarity)=1 (here the most four similar words are used)
* [useCamelCaseFeature] ()=1//TODO///////////////////////////////////////////////////////
* [useDBPediaPersonListFeature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#other-gazetteers)=1
* [useDBPediaLocationListFeature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#other-gazetteers)=1
* [useDBPediaPersonLastNameFeature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#other-gazetteers)=1
* [useTopicClass100Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)=1 (here only 100 Topic Clusters are used)
* [useTopicClass50Feature](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)=1 (here only 50 Topic Clusters are used)
* [useTopicClass200Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)=1 (here only 200 Topic Clusters are used)
* [useTopicClass500Feature] (https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#topic-clusters)=1 (here only 500 Topic Clusters are used)=1
* useTopicClassUpper100Feature=1//TODO///////////////////////////////////////////////////////
* [useCharacterCategoryFeature](https://github.com/tudarmstadt-lt/GermaNER/wiki/Features#character-category-pattern)=1