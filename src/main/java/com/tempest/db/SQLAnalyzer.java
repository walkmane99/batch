package com.tempest.db;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

public class SQLAnalyzer {
    public final static char QUOTE = '\'';
    public final static char DOUBLE_QUOTE = '"';

    SQLAnalyzer() {}

    public static void main(String ...args){
        SQLAnalyzer analyzer = new SQLAnalyzer();
        analyzer.analyze("SELECT aaa FROM bbb b inner join www w on (b.r = w.r) WHERE xx=${name} AND A.b='test' and a.x=9");

    }

    void analyze(String sql) {
        StringReader fr = new StringReader(sql);
        StreamTokenizer tokenizer = new StreamTokenizer(fr);
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('$', '$');
        tokenizer.wordChars('{', '{');
        tokenizer.wordChars('}', '}');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.whitespaceChars('\r', '\r');
        tokenizer.quoteChar(QUOTE);
        tokenizer.quoteChar(DOUBLE_QUOTE);
        //tokenizer.parseNumbers();
        tokenizer.eolIsSignificant(false);
        tokenizer.slashStarComments(true);
        tokenizer.slashSlashComments(true);
        int token = 0;
        try {
            while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                    case StreamTokenizer.TT_EOL:
                        System.out.println("<EOL/>");
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.println("<number>" + tokenizer.nval + "</number>");
                        break;
                    case StreamTokenizer.TT_WORD:
                        System.out.println("<word>" + tokenizer.sval + "</word>");
                        break;
                    case QUOTE:
                        System.out.println("<char>" + tokenizer.sval + "</char>");
                        break;
                    case DOUBLE_QUOTE:
                        System.out.println("<string>" + tokenizer.sval + "</string>");
                        break;
                    default:
                        if (((char) tokenizer.ttype) == '-') {
                            System.out.println("<string defalt>" + String.valueOf((char) tokenizer.ttype) + "</string>");
                        } else if (((char) tokenizer.ttype) == '(') {
                            System.out.println("<string defalt2>" + tokenizer.sval + "</string>");
                        } else if (((char) tokenizer.ttype) == ')') {
                            System.out.println("<string defalt3>" + tokenizer.sval + "</string>");
                        }
                        System.out.print("<token>" + (char) tokenizer.ttype + "</token>");
                }
            }
        } catch ( IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
