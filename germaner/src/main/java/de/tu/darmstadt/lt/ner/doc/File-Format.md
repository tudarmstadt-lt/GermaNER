# Training data

The training data should be singe TAB separated, where the first column is the word, and the last column (columns in between will be ignored) is the class of the word. Sentences are marked by a blank new line. The builtin GermaNER is based on the **[GermEval 2014](https://sites.google.com/site/germeval2014ner/)** dataset and where only 4 named entity classes, namely **`ORG`**, **`PER`**, **`LOC`**, and **`OTH`** are used.
The following is an example of how the training file looks like (tab separated token and lables, sentences separated by new line). The full training file is found [here](https://github.com/tudarmstadt-lt/GermaNER/releases/download/germaNER0.9.1/full_train.tsv)

|              |       | 
|--------------|-------| 
| Schartau     | B-PER | 
| sagte        | O     | 
| dem          | O     | 
| "            | O     | 
| Tagesspiegel | B-ORG | 
| "            | O     | 
| vom          | O     | 
| Freitag      | O     | 
| ,            | O     | 
| Fischer      | B-PER | 
| sei          | O     | 
| "            | O     | 
| in           | O     | 
| einer        | O     | 
| Weise        | O     | 
| aufgetreten  | O     | 
| ,            | O     | 
| die          | O     | 
| alles        | O     | 
| andere       | O     | 
| als          | O     | 
| überzeugend  | O     | 
| war          | O     | 
| "            | O     | 
| .            | O     | 



|                |       | 
|----------------|-------| 
| Firmengründer  | O     | 
| Wolf           | B-PER | 
| Peter          | I-PER | 
| Bree           | I-PER | 
| arbeitete      | O     | 
| Anfang         | O     | 
| der            | O     | 
| siebziger      | O     | 
| Jahre          | O     | 
| als            | O     | 
| Möbelvertreter | O     | 
| ,              | O     | 
| als            | O     | 
| er             | O     | 
| einen          | O     | 
| fliegenden     | O     | 
| Händler        | O     | 
| aus            | O     | 
| dem            | O     | 
| Libanon        | B-LOC | 
| traf           | O     | 
| .              | O     | 


# Test Data

The test data supported is a one TAB separated file, where the first column is a single word and other columns, if exist, are ignored. Sentence is marked by a blank new line.

The following example shows a two sentence test file, where the sentences are separated by a new line.


Gleich  
darauf  
entwirft  
er  
seine  
Selbstdarstellung  
"  
Ecce  
homo  
"  
in  
enger  
Auseinandersetzung  
mit  
diesem  
Bild  
Jesu  

Und  
in  
seinen   
letzten  
Briefen  
tritt  
er  
schließlich  
selbst  
in  
diese  
Erlöserrolle  
ein  
.  
 
