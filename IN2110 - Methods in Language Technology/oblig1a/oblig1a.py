#!/usr/bin/env python
# coding: utf-8

# # IN2110 Oblig 1 pre-kode

# In[14]:


# Klasser og funksjoner fra scikit-learn som vi skal bruke i obligen
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score

# Norec dataset
from in2110.corpora import norec

# Hjelpefunksjoner for visualisering
from in2110.oblig1 import scatter_plot

# Andre imports
import nltk


# In[15]:


def prepare_data(documents):
    """Tar inn en iterator (kan være en liste) over dokumenter fra norec
    og returnerer to lister:

    - data   : En liste over dokument-tekstene.
    - labels : En liste over hvilken kategori dokumentet tilhører.

    Begge listene skal være like lange og for dokumentet i data[i]
    skal vi kunne finne kategorien i labels[i].
    """

    # Din kode her

    data = []
    labels = []
    
    for document in documents:
        if document.metadata['category'] in ['games', 'literature', 'restaurants']:
            data.append(document.text)
            labels.append(document.metadata['category'])
    
    return data, labels


# In[16]:


""" Redeklarasjonen av følgende funksjon illustrerer prosessen med utprøving av ulike preprosesseringsmetoder.
    Funksjonene inneholder en kommentar med navnet metoden har fått i rapporten """

def tokenize(text):
    """Tar inn en streng med tekst og returnerer en liste med tokens."""

    # Å splitte på mellomrom er fattigmanns tokenisering. Endre til noe
    # bedre!

    return text.split()

def tokenize(text):
    # NLTK-tokenisering
    return nltk.word_tokenize(text)

def tokenize(text):
    # Små bokstaver, NLTK-tokenisering
    return nltk.word_tokenize(text.lower())

def tokenize(text):
    # Splitt etter regelsettt
    return nltk.tokenize.TreebankWordTokenizer().tokenize(text)

def tokenize(text):
    # Lemmatisering, regelsett
    tokens = nltk.tokenize.TreebankWordTokenizer().tokenize(text)
    stemmer = nltk.stem.WordNetLemmatizer()
    return " ".join(stemmer.lemmatize(token) for token in tokens)

def tokenize(text):
    # Stemming, regelsett
    tokens = nltk.tokenize.TreebankWordTokenizer().tokenize(text)
    stemmer = nltk.stem.PorterStemmer()
    return [stemmer.stem(token) for token in tokens]


# In[17]:


class Vectorizer(object):
    def __init__(self):
        """Konstruktør som tar inn antall klasser som argument."""

        self.vectorizer = CountVectorizer(lowercase=False, tokenizer=tokenize, analyzer='word', max_features=5000)
        self.tfidf = TfidfTransformer()

    def vec_train(self, data):
        """Tilpass vektorisereren til treningsdata. Returner de vektoriserte
        treningsdataene med og uten tfidf-vekting.

        """

        # Din kode her

        # Tips: Bruk fit_transform() for å spare kjøretid.

        vec = self.vectorizer.fit_transform(data).toarray()
        vec_tfidf = self.tfidf.fit_transform(vec)

        return vec, vec_tfidf

    def vec_test(self, data):
        """Vektoriser dokumentene i nye data. Returner vektorer med og uten
        tfidf-vekting.

        """

        # Din kode her

        vec = self.vectorizer.transform(data).toarray()
        vec_tfidf = self.tfidf.transform(vec)

        return vec, vec_tfidf


# In[18]:


def create_knn_classifier(vec, labels, k):
    """Lag en k-NN-klassifikator, tren den med vec og labels, og returner
    den.

    """

    clf = KNeighborsClassifier(n_neighbors=k)
    clf.fit(vec, labels)

    return clf


# In[19]:


# Treningsdata
print('Preparing training data', end='... ')
train_data, train_labels = prepare_data(norec.train_set())
print('done!')

# Valideringsdata
print('Preparing dev data', end='... ')
dev_data, dev_labels = prepare_data(norec.dev_set())
print('done!')

# Testdata
print('Preparing test data', end='... ')
test_data, test_labels = prepare_data(norec.test_set())
print('done!')


# In[20]:


# Opprett og tren vektorisereren
vectorizer = Vectorizer()
print('Vectorizing training data')
train_vec, train_vec_tfidf = vectorizer.vec_train(train_data)
print('Vectorizing dev data')
dev_vec, dev_vec_tfidf = vectorizer.vec_test(dev_data)
print('Vectorizing test data')
test_vec, test_vec_tfidf = vectorizer.vec_test(test_data)


# In[21]:


# Oppgave 3c - Opprett klassifikatoren, prediker testsettet, og print accuracy_score
clf = create_knn_classifier(train_vec_tfidf, train_labels, 20)
print('Predicting...')
prediction = clf.predict(test_vec_tfidf)
print('Accuracy score: %.3f' %  accuracy_score(test_labels, prediction))

