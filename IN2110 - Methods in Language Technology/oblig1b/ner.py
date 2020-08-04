import re

class NamedEntityRecogniser:
    """Gjenkjenning av navngitte enheter ved bruk av HMM"""
    
    def __init__(self):
        """Intialiserer alle variablene som er nødvendig for å representere og 
        estimere  sekvensmodellen (en Hidden Markov Model) som brukes til å 
        gjenkjenne de navngitte enhetene"""
        
        # alle labellene som forekommer i treningsettet
        self.labels = set()

        # alle token som forekommer i treningsettet
        self.vocab = set()

        # hvor mange ganger en label (f.eks. B-ORG) forekommer i treningsettet
        self.label_counts = {}  

        # hvor mange transisjoner fra label_1 til label2 forekommer i treningsettet
        self.transition_counts = {}
        
        # hvor mange emisjoner fra label til token forekommer i treningsettet
        # (Merk at vi legger et spesielt symbol for ord som aldri forekommer
        # i treningsettet, men kan forekomme i testsettet)
        self.emission_counts = {("O", "<UNK>"):1}
                
        # Sansynnlighet P(label_2 | label_1)
        self.transition_probs = {}
        
        # Sansynnlighet P(token | label)
        self.emission_probs = {}
    
    
    def fit(self, tagged_text):
        """Estimerer tallene og sansynnlighetene for HMM, basert på (tokenisert)
        tekst hvor navngitte enhetene er markert med XML tags (se norne.txt)"""
        
        # Ekstrahere setninger og navngitte enheter markert i hver setning
        sentences, all_spans = preprocess(tagged_text)
                
        for sentence, spans in zip(sentences, all_spans):
     
            # Ekstrahere labelsekvenser, med BIO (også kalt IOB) marking
            label_sequence = get_BIO_sequence(spans, len(sentence))
                                              
            # Oppdatere tallene 
            self._add_counts(sentence, label_sequence)
            
        # Beregne sansynnlighetene (transition og emission) ut fra tallene
        self._fill_probs()
                   
        
    def _add_counts(self, sentence, label_sequence):
        """Oppdaterer variablene self.vocab, self.labels, self.label_counts, 
        self.transition_counts og  self.emission_counts, basert på setningen og 
        sekvenslabellen assosiert med dem. 
        Merk at setningen og label_sequence har samme lengde."""
        
        raise NotImplementedException()
        
    def _fill_probs(self, alpha_smoothing=1E-6):
        """Beregne sannsynlihetsfordelinger self.transition_probs og
        self.emission_probs basert på tallene som er samlet inn i 
        self.label_counts, self.transition_counts og self.emission_counts.
        
        Når det gjeler self.emission_probs bør vi legge Laplace smoothing, med en
        verdi for alpha som er alpha_smoothing."""
        
        raise NotImplementedException()
            
    
    def _viterbi(self, sentence):
        """Kjører Viterbi-algoritmen på setningen (liste over tokens), og
        returnerer to outputs: 
        1) en labelsekvens (som har samme lengde som setningen)
        2) sansynnlighet for hele sekvensen """

        # De 2 datastrukturer fra Viterbi algoritmen, som dere må fylle ut
        lattice = [{label:None for label in self.labels}
                           for _ in range(len(sentence))]
        backpointers = [{label:None for label in self.labels}
                        for _ in range(len(sentence))]

        # Fylle ut lattice og backpointers for setningen
        for i, token in enumerate(sentence):
            for label in self.labels:
                raise NotImplementedException()

        # Finne ut det mest sannsynlig merkelapp for det siste ordet
        best_final_label = max(lattice[-1].keys(), key=lambda x: lattice[-1][x])
        best_final_prob = lattice[-1][best_final_label]

        # Ekstrahere hele sekvensen ved å følge de "backpointers"
        best_path = [best_final_label]
        for i in range(i,0,-1):
            best_path.insert(0, backpointers[i][best_path[0]])

        # Returnerer den mest sannsynlige sekvensen (og dets sannsynlighet)
        return best_path, best_final_prob
 
                
                
    def label(self, text):
        """Gitt en tokenisert tekst, finner ut navngitte enheter og markere disse
        med XML tags. """
        sentences, _ = preprocess(text)
        spans = []
        for sentence in sentences:
            sentence = [token if token in self.vocab else "<UNK>" for token in sentence]
            label_sequence, _ = self._viterbi(sentence)
            spans.append(get_spans(label_sequence))
        
        return postprocess(sentences, spans)
            

    
def get_BIO_sequence(spans, sentence_length):
    """Gitt en liste over "spans", representert som tuples (start, end, tag),
    og en setningslengde, produserer en sekvens med BIO (også kalt IOB) labeller
    for setningen. 
    Eksempel: hvis spans=[(1,3,'ORG')] og sentence_length=6 bør resultatet være
    ['O', 'B-ORG', 'I-ORG', 'O', 'O', 'O']"""
    
    res = ['O'] * sentence_length
    for span in spans:
        res[span[0]] = 'B-' + span[2]
        for n in range(span[0]+1, span[2]):
            res[n] = 'I-' + span[2]
    return res
                    

def get_spans(label_sequence):
    """Gitt en labelsekvens med BIO markering, returner en lister over "spans" med 
    navngitte enheter. Metoden er altså den motsatte av get_BIO_sequence"""
    
    spans = []           
    i = 0
    while i < len(label_sequence):
        label = label_sequence[i]
        if label.startswith("B-"):
            start = i
            label = label[2:]
            end = start + 1
            while end < len(label_sequence) and label_sequence[end].startswith("I-%s"%label):
                end += 1
            spans.append((start, end, label))
            i = end
        else:
            i += 1
    return spans


def preprocess(tagged_text):
    """Tar en tokenisert tekst med XML tags (som f.eks. <ORG>Stortinget</ORG>) og
    returnerer en liste over setninger (som selv er lister over tokens), sammen med
    en liste av samme lengde som inneholder de markerte navngitte enhetene. """
    
    sentences = []
    spans = []
    
    for i, line in enumerate(tagged_text.split("\n")):

        tokens = []
        spans_in_sentence = []
        
        for j, token in enumerate(line.split(" ")):
            
            # Hvis token starter med en XML tag
            start_match = re.match("<(\w+?)>", token)
            if start_match:
                new_span = (j, None, start_match.group(1))
                spans_in_sentence.append(new_span)
                token = token[start_match.end(0):]
            
            # Hvis token slutter med en XML tag
            end_match = re.match("(.+)</(\w+?)>$", token)
            if end_match:
                if not spans_in_sentence or spans_in_sentence[-1][1]!=None:
                    raise RuntimeError("Closing tag without corresponding open tag")
                start, _ , tag = spans_in_sentence[-1]
                if tag != end_match.group(2):
                    raise RuntimeError("Closing tag does not correspond to open tag")
                token = token[:end_match.end(1)]
                spans_in_sentence[-1] = (start, j+1, tag)
                
            tokens.append(token)
            
        sentences.append(tokens)
        spans.append(spans_in_sentence)
        
    return sentences, spans


def postprocess(sentences, spans):
    """Gitt en liste over setninger og en tilsvarende liste over "spans" med
    navngitte enheter, produserer en tekst med XML markering."""
    
    tagged_sentences = []
    for i, sentence in enumerate(sentences):
        new_sentence = list(sentence)
        for start, end, tag in spans[i]:
            new_sentence[start] = "<%s>%s"%(tag, new_sentence[start])
            new_sentence[end-1] = "%s</%s>"%(new_sentence[end-1], tag)
        tagged_sentences.append(" ".join(new_sentence))
     
    return "\n".join(tagged_sentences)

        
    
