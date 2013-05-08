/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar aiml;

@parser::members {
    boolean opened = false;
    public enum router {AIML,BR,STAR,A,BOT,CONDITION,PERSON2,ID,VERSION,TEMPLATE,TOPIC,CATEGORY,PATTERN,PERSON,GET,INPUT,SET,SRAI,THAT,RANDOM,LI,FORMAL,THINK,UNKNOWN};
    public router decode(String value) {
        if("aiml".compareTo(value)==0) return router.AIML;
        if("template".compareTo(value)==0) return router.TEMPLATE;
        if("topic".compareTo(value)==0) return router.TOPIC;
        if("category".compareTo(value)==0) return router.CATEGORY;
        if("pattern".compareTo(value)==0) return router.PATTERN;
        if("get".compareTo(value)==0) return router.GET;
        if("srai".compareTo(value)==0) return router.SRAI;
        if("that".compareTo(value)==0) return router.THAT;
        if("random".compareTo(value)==0) return router.RANDOM;
        if("li".compareTo(value)==0) return router.LI;
        if("formal".compareTo(value)==0) return router.FORMAL;
        if("think".compareTo(value)==0) return router.THINK;
        if("set".compareTo(value)==0) return router.SET;
        if("input".compareTo(value)==0) return router.INPUT;
        if("person".compareTo(value)==0) return router.PERSON;
        if("Br".compareTo(value)==0) return router.BR;
        if("star".compareTo(value)==0) return router.STAR;
        if("a".compareTo(value)==0) return router.A;
        if("bot".compareTo(value)==0) return router.BOT;
        if("condition".compareTo(value)==0) return router.CONDITION;
        if("person2".compareTo(value)==0) return router.PERSON2;
        if("id".compareTo(value)==0) return router.ID;
        if("version".compareTo(value)==0) return router.VERSION;
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