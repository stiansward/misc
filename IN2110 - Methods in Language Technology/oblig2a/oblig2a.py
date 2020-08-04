import spacy
from in2110.conllu import ConlluDoc

def attachment_score(true, pred):
    w_correct_head = 0
    w_correct_head_and_dep = 0
    words = 0
    for t, p in zip(true, pred):
        for wt, wp in zip(t, p):
            words += 1
            if str(wt.head) == str(wp.head):
                w_correct_head += 1
                if wt.dep_ == wp.dep_:
                    w_correct_head_and_dep += 1
    las = w_correct_head_and_dep / words
    uas = w_correct_head / words

    return uas, las

print('Laster modell...')
nb = spacy.load('model/model-best')
conllu_dev = ConlluDoc.from_file('no_bokmaal-ud-dev.conllu')
dev_docs = conllu_dev.to_spacy(nb)
dev_docs_unlabeled = conllu_dev.to_spacy(nb, keep_labels=False)

print('Scores for Bokmål...')
uas, las = attachment_score(dev_docs, [nb.parser(x) for x in dev_docs_unlabeled])
print('UAS :', uas)
print('LAS :', las)

print('Scores for Nynorsk...')
nynorsk_dev = ConlluDoc.from_file('no_nynorsk-ud-dev.conllu')
nn_dev_docs = nynorsk_dev.to_spacy(nb)
nn_dev_docs_unlabeled = nynorsk_dev.to_spacy(nb, keep_labels=False)
uas, las = attachment_score(nn_dev_docs, [nb.parser(x) for x in nn_dev_docs_unlabeled])
print('UAS :', uas)
print('LAS :', las)

print('Scores for NynorskLIA...')
nynorsklia_dev = ConlluDoc.from_file('no_nynorsklia-ud-dev.conllu')
nnlia_dev_docs = nynorsklia_dev.to_spacy(nb)
nnlia_dev_docs_unlabeled = nynorsklia_dev.to_spacy(nb, keep_labels=False)
uas, las = attachment_score(nnlia_dev_docs, [nb.parser(x) for x in nnlia_dev_docs_unlabeled])
print('UAS :', uas)
print('LAS :', las)