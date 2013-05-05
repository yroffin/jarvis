/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar aiml;

@parser::members {
    boolean opened = false;
    public enum router {AIML,TEMPLATE,TOPIC,CATEGORY,PATTERN,GET,UNKNOWN};
    public router decode(String value) {
        if("aiml".compareTo(value)==0) return router.AIML;
        if("template".compareTo(value)==0) return router.TEMPLATE;
        if("topic".compareTo(value)==0) return router.TOPIC;
        if("category".compareTo(value)==0) return router.CATEGORY;
        if("pattern".compareTo(value)==0) return router.PATTERN;
        if("get".compareTo(value)==0) return router.GET;
        return router.UNKNOWN;
    }
    public void onOpenTag(String value) {
        System.err.println("must be overriden !!!");
    }
    public void onCloseTag(String value) {
        System.err.println("must be overriden !!!");
    }
    public void onPcData(String value) {
        System.err.println("must be overriden !!!");
    }
}

@lexer::members {
    boolean tagMode = false;
}

tokens {
    AIML
}

document  : element ;
 
element
    : startTag
        (element
        | PCDATA {onPcData($PCDATA.text);}
        )*
        endTag
    | emptyElement
    ;

startTag  : TAG_START_OPEN namedspace? aimlOpenTag (attribute)* {onOpenTag($aimlOpenTag.text);} TAG_CLOSE ;

attribute  : genericAttrId ATTR_EQ ATTR_VALUE ;

endTag :  TAG_END_OPEN namedspace? aimlCloseTag {onCloseTag($aimlCloseTag.text);} TAG_CLOSE ;

emptyElement : TAG_START_OPEN namedspace? aimlEmptyTag {onOpenTag($aimlEmptyTag.text);} (attribute)* {onCloseTag($aimlEmptyTag.text);} TAG_EMPTY_CLOSE ;

genericAttrId
    :
      namedspace? aimlAttribute
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

LETTERS
    : ('a'..'z'| 'A'..'Z')+ SEMICOLON
    ;

NAMECHAR
    : (LETTER | DIGIT | '.' | '-')+
    ;

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

WS  :  { tagMode }?
       (' '|'\r'|'\t'|'\u000C'|'\n') ->skip
;