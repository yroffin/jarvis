/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar aiml;

@parser::members {
    boolean opened = false;
    public enum router {GOSSIP,GENDER,SYSTEM,JAVASCRIPT,UPPER,LOWER,SIZE,DATE,AIML,BR,STAR,A,BOT,CONDITION,PERSON2,ID,VERSION,TEMPLATE,TOPIC,CATEGORY,PATTERN,PERSON,GET,INPUT,SET,SR,SRAI,THAT,RANDOM,LI,FORMAL,THINK,THATSTAR,TOPICSTAR,UNKNOWN};
    public router decode(String value) {
        if("aiml".compareTo(value.toLowerCase())==0) return router.AIML;
        if("system".compareTo(value.toLowerCase())==0) return router.SYSTEM;
        if("javascript".compareTo(value.toLowerCase())==0) return router.JAVASCRIPT;
        if("template".compareTo(value.toLowerCase())==0) return router.TEMPLATE;
        if("topic".compareTo(value.toLowerCase())==0) return router.TOPIC;
        if("category".compareTo(value.toLowerCase())==0) return router.CATEGORY;
        if("pattern".compareTo(value.toLowerCase())==0) return router.PATTERN;
        if("get".compareTo(value.toLowerCase())==0) return router.GET;
        if("srai".compareTo(value.toLowerCase())==0) return router.SRAI;
        if("sr".compareTo(value.toLowerCase())==0) return router.SR;
        if("that".compareTo(value.toLowerCase())==0) return router.THAT;
        if("random".compareTo(value.toLowerCase())==0) return router.RANDOM;
        if("li".compareTo(value.toLowerCase())==0) return router.LI;
        if("formal".compareTo(value.toLowerCase())==0) return router.FORMAL;
        if("think".compareTo(value.toLowerCase())==0) return router.THINK;
        if("set".compareTo(value.toLowerCase())==0) return router.SET;
        if("input".compareTo(value.toLowerCase())==0) return router.INPUT;
        if("person".compareTo(value.toLowerCase())==0) return router.PERSON;
        if("br".compareTo(value.toLowerCase())==0) return router.BR;
        if("star".compareTo(value.toLowerCase())==0) return router.STAR;
        if("a".compareTo(value.toLowerCase())==0) return router.A;
        if("bot".compareTo(value.toLowerCase())==0) return router.BOT;
        if("condition".compareTo(value.toLowerCase())==0) return router.CONDITION;
        if("person2".compareTo(value.toLowerCase())==0) return router.PERSON2;
        if("id".compareTo(value.toLowerCase())==0) return router.ID;
        if("version".compareTo(value.toLowerCase())==0) return router.VERSION;
        if("thatstar".compareTo(value.toLowerCase())==0) return router.THATSTAR;
        if("topicstar".compareTo(value.toLowerCase())==0) return router.TOPICSTAR;
        if("date".compareTo(value.toLowerCase())==0) return router.DATE;
        if("size".compareTo(value.toLowerCase())==0) return router.SIZE;
        if("uppercase".compareTo(value.toLowerCase())==0) return router.UPPER;
        if("lowercase".compareTo(value.toLowerCase())==0) return router.LOWER;
        if("gender".compareTo(value.toLowerCase())==0) return router.GENDER;
        if("gossip".compareTo(value.toLowerCase())==0) return router.GOSSIP;
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

startTag  : TAG_START_OPEN namedspace? aimlOpenTag {onOpenTag($aimlOpenTag.text);} (attribute)* TAG_CLOSE ;

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
    : { tagMode }? (LETTER | DIGIT | MISC)+
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

/**
 * Authorized tokens in value
 */
fragment
MISC : DOT | PLUS | MINUS | LPARENT | RPARENT | OTHERS;
fragment DOT: '.';
fragment SEMICOLON: ':';
fragment PLUS: '+';
fragment MINUS: '-';
fragment LPARENT: '(';
fragment RPARENT: ')';
fragment OTHERS : 'É' | 'È' | 'Ê' | 'À' | 'Ô' | 'Û' | 'Ç' | 'Ë' | 'Â' | 'Î' | 'Ï' | 'Ù' | 'Ö' |  'é' | 'è' | 'ê' | 'à' | 'ô' | 'û' | 'ç' | 'ë' | 'â' | 'î' | 'ï' | 'ù' | 'ö';

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