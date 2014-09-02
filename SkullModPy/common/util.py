def tag(tag_string, closing=True):
    return '</' + tag_string + '>' if closing else '<' + tag_string + '>'

