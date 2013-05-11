/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar normalizer;

@parser::members {
    public void onNewSentence() {
        System.out.println("onNewSentence");
    }
    public void onNewWord(String value) {
        System.out.println("onNewWord - ["+value+"]");
    }
    public void onNewFilename(String value) {
        System.out.println("onNewFilename - ["+value+"]");
    }
    public void onNewUrl(String value) {
        System.out.println("onNewUrl - ["+value+"]");
    }
    public void onNewAbrev(String value) {
        System.out.println("onNewAbrev - ["+value+"]");
    }
}

@lexer::members {
}

tokens {
}

document : (sentence (DOT|QUESTIONMARK|EXCLAIM)?)+ EOF;

sentence : {onNewSentence();} word+;

word
    : simpleword
    | filename
    | url
    | abrev
    | misc;

simpleword : SIMPLEWORD {onNewWord($SIMPLEWORD.text);};
filename : FILENAME {onNewFilename($FILENAME.text);};
url : URL {onNewUrl($URL.text);};
abrev : ABREV {onNewAbrev($ABREV.text);};
misc : COMMA | SEMICOLON | MINUS | LPARENT | RPARENT | COTE;

DOT: '.';
QUESTIONMARK: '?';
EXCLAIM: '!';
COMMA: ',';
SEMICOLON: ':';
MINUS: '-';
LPARENT: '(';
RPARENT: ')';
COTE: '\'';

COMMENT : '<!--' .*? '-->' -> skip ;

SIMPLEWORD
    : LETTERS+
    ;

FILENAME
    : (LETTERS)+ DOT (LETTERS)+
    ;

ABREV
    : 'That\'s' | 'don\'t'
    ;

URL
    : ('http://' | 'HTTP://') (LETTERS)+ ('.' (LETTERS))* ('/' (LETTERS))*
    ;

fragment LETTERS
    : LETTER+
    ;

fragment DIGIT
    :    '0'..'9'
    ;
 
fragment LETTER
    : 'a'..'z'
    | 'A'..'Z'
    ;

WS
    : (','|' '|'\r'|'\t'|'\u000C'|'\n') ->skip
    ;