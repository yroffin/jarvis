/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar aiml;

@parser::members {
    boolean opened = false;
    public enum router {AIML,TEMPLATE,TOPIC,CATEGORY,PATTERN,GET,SRAI,UNKNOWN};
    public router decode(String value) {
        if("aiml".compareTo(value)==0) return router.AIML;
        if("template".compareTo(value)==0) return router.TEMPLATE;
        if("topic".compareTo(value)==0) return router.TOPIC;
        if("category".compareTo(value)==0) return router.CATEGORY;
        if("pattern".compareTo(value)==0) return router.PATTERN;
        if("get".compareTo(value)==0) return router.GET;
        if("srai".compareTo(value)==0) return router.SRAI;
        return router.UNKNOWN;
    }
    public void onOpenTag(String value) {
        /**
         * must be overriden
         */
    }
    public void onCloseTag(String value) {
        /**
         * must be overriden
         */
    }
    public void onPcData(String value) {
        /**
         * must be overriden
         */
    }
    public void onAttribute(String key, String value) {
        /**
         * must be overriden
         */
    }
}

@lexer::members {
    boolean tagMode = false;
}

tokens {
    AIML
}

document  : header element ;

header
    : TAG_START_HEADER (attribute)+ TAG_END_HEADER
    ;

element
    : startTag (pcData | element)* endTag
    | emptyElement
    ;

pcData : PCDATA {onPcData($PCDATA.text);}
       ;

startTag  : TAG_START_OPEN namedspace? aimlOpenTag (attribute)* {onOpenTag($aimlOpenTag.text);} TAG_CLOSE ;

attribute  : genericAttrId ATTR_EQ ATTR_VALUE {onAttribute($genericAttrId.text, $ATTR_VALUE.text);};

endTag :  TAG_END_OPEN namedspace? aimlCloseTag {onCloseTag($aimlCloseTag.text);} TAG_CLOSE ;

emptyElement : TAG_START_OPEN namedspace? aimlEmptyTag {onOpenTag($aimlEmptyTag.text);} (attribute)* {onCloseTag($aimlEmptyTag.text);} TAG_EMPTY_CLOSE ;

genericAttrId
    : namedspace? aimlAttribute
    ;

namedspace
    : LETTERS
    ;

aimlAttribute
    : NAMECHAR
    ;

aimlOpenTag
    : NAMECHAR
    ;

aimlCloseTag
    : NAMECHAR
    ;

aimlEmptyTag
    : NAMECHAR
    ;

COMMENT : '<!--' .*? '-->' -> skip ;

LETTERS
    : ('a'..'z'| 'A'..'Z')+ SEMICOLON
    ;

NAMECHAR
    : { tagMode }? (LETTER | DIGIT | '.' | '-')+
    ;

TAG_START_HEADER : '<?xml' { tagMode = true; };
TAG_END_HEADER : { tagMode }? '?>' (' '|'\r'|'\t'|'\u000C'|'\n')* { tagMode = false; };

TAG_START_OPEN : '<' { tagMode = true; } ;
TAG_END_OPEN : '</' { tagMode = true; } ;
TAG_CLOSE : { tagMode }? '>' { tagMode = false; } ;
TAG_EMPTY_CLOSE : { tagMode }? '/>' { tagMode = false; } ;
 
ATTR_EQ : {tagMode }? '=' ;
 
ATTR_VALUE : { tagMode }?
        ( '"' (~'"')* '"'
        | '\'' (~'\'')* '\''
        )
    ;
 
PCDATA : { !tagMode }? (~'<')+ ;

fragment SEMICOLON
    : ':'
    ;

fragment DIGIT
    :    '0'..'9'
    ;
 
fragment LETTER
    : 'a'..'z'
    | 'A'..'Z'
    ;

WS
    : { tagMode }? (' '|'\r'|'\t'|'\u000C'|'\n') ->skip
    ;