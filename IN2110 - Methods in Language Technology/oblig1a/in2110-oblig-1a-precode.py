# IN2110 Oblig 1 pre-kode

# Klasser og funksjoner fra scikit-learn som vi skal bruke i obligen
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score

# Norec dataset
from in2110.corpora import norec

# Hjelpefunksjoner for visualisering
from in2110.oblig1 import scatter_plot

def prepare_data(documents):
    """Tar inn en iterator (kan være en liste) over dokumenter fra norec
    og returnerer to lister:

    - data   : En liste over dokument-tekstene.
    - labels : En liste over hvilken kategori dokumentet tilhører.

    Begge listene skal være like lange og for dokumentet i data[i]
    skal vi kunne finne kategorien i labels[i].
    """

    # Din kode her

    return data, labels

def tokenize(text):
    """Tar inn en streng med tekst og returnerer en liste med tokens."""

    # Å splitte på mellomrom er fattigmanns tokenisering. Endre til noe
    # bedre!

    return text.split()

class Vectorizer(object):
    def __init__(self):
        """Konstruktør som tar inn antall klasser som argument."""

        self.vectorizer = None
        self.tfidf = None

    def vec_train(self, data):
        """Tilpass vektorisereren til treningsdata. Returner de vektoriserte
        treningsdataene med og uten tfidf-vekting.

        """

        vec = None
        vec_tfidf = None

        # Din kode her

        # Tips: Bruk fit_transform() for å spare kjøretid.

        return vec, vec_tfidf

    def vec_test(self, data):
        """Vektoriser dokumentene i nye data. Returner vektorer med og uten
        tfidf-vekting.

        """

        vec = None
        vec_tfidf = None

        # Din kode her

        return vec, vec_tfidf

def create_knn_classifier(vec, labels, k):
    """Lag en k-NN-klassifikator, tren den med vec og labels, og returner
    den.

    """

    clf = None

    return clf

# Treningsdata
train_data, train_labels = prepare_data(norec.train_set())

# Valideringsdata
dev_data, dev_labels = prepare_data(norec.dev_set())

# Testdata
test_data, test_labels = prepare_data(norec.test_set())

# Din kode her
