from in2110.oblig1b import visualize_word_vectors
from in2110.corpora import aviskorpus_10_nn

def preprocess(sentences):
    """Return list of preprocessed tokens."""

    return []

def context_window(sent, pos, size):
    """Return context window for word at pos of given size."""

    return []

class WordVectorizer(object):
    """Word vectorizer with sklearn-compatible interface."""

    def __init__(self, max_features, window_size, normalize=False):
        self.max_features = max_features
        self.window_size = window_size
        self.normalize = normalize
        self.matrix = None
        self.is_normalized = False

    def fit(self, sentences):
        """Fit vectorizer to sentences."""

        pass

    def transform(self, words):
        """Return vectors for each word in words."""

        return [self.matrix[w] for w in words]

    def vector_norm(self, word):
        """Compute vector norm for word."""

        return 0

    def normalize_vectors(self):
        """Normalize vectors."""

        pass

    def euclidean_distance(self, w1, w2):
        """Compute euclidean distance between w1 and w2."""

        return 0

    def cosine_similarity(self, w1, w2):
        """Compute cosine similarity between w1 and w2."""

        return 0

    def nearest_neighbors(self, w, k=5):
        """Return list of the k nearest neighbors to w."""

        return []
