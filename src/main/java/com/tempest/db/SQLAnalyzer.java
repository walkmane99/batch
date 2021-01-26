package com.tempest.db;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SQLAnalyzer {
    public final static char QUOTE = '\'';
    public final static char DOUBLE_QUOTE = '"';



    private enum QUERY_TYPE {
        SELECT,UPDATE,INSERT,DELETE;
    }

    private int count;
    private QUERY_TYPE type;
    private String sql;

    private Map<Integer,String> names;

    SQLAnalyzer() {
        this.names = new HashMap<>();
    }

    public static void main(String... args) {
        SQLAnalyzer analyzer = new SQLAnalyzer();
        analyzer.analyze(
                "SELECT aaa FROM bbb b inner join www w on (b.r = w.r) WHERE xx=${name} AND A.b='test' and a.x=9");

    }

    void analyze(String sql) {
        this.sql = sql;
        StringReader fr = new StringReader(sql);
        StreamTokenizer tokenizer = new StreamTokenizer(fr);
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('$', '$');
        tokenizer.wordChars('{', '{');
        tokenizer.wordChars('}', '}');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('(', ')');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.whitespaceChars('\r', '\r');
        //tokenizer.quoteChar(QUOTE);
        //tokenizer.quoteChar(DOUBLE_QUOTE);
        // tokenizer.parseNumbers();
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
                    // case StreamTokenizer.TT_NUMBER:
                    // System.out.println("<number>" + tokenizer.nval + "</number>");
                    // break;
                    case StreamTokenizer.TT_WORD:
                        word(tokenizer.sval);
                        System.out.println("<word>" + tokenizer.sval + "</word>");
                        break;
//                    case QUOTE:
//                        System.out.println("<char>" + tokenizer.sval + "</char>");
//                        break;
//                    case DOUBLE_QUOTE:
//                        System.out.println("<string>" + tokenizer.sval + "</string>");
//                        break;
                    default:
                        System.out.print("<token>" + (char) tokenizer.ttype + "</token>");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.names.entrySet().stream().forEach(x -> System.out.println(x.getKey() + " " + x.getValue()));
    }

    private void word(String val) {
        if (this.type == null) {
            // 構文のtypeを知ること
            this.setType(val);
            return;
        }
        // ${zzz} を取得すること先頭から続くのでその位置を持つこと、名前とindexの組み合わせで覚えておく
        if (val.indexOf("${") != -1) {
            //TODO: in (${zzz}) だった場合、リストのサイズをどこから持ってくるか？
            this.count++;
            this.names.put(this.count, val.substring(2, val.length() -1));
        }
    }
    private void setType(String val) {
        try {
            QUERY_TYPE type = QUERY_TYPE.valueOf(val.toUpperCase(Locale.ROOT));
            if (type != null) {
                this.type = type;
            }
        }catch (IllegalArgumentException e) {
            System.out.println("typeが存在しない。");
        }
    }

    public boolean isSelect() {
        return this.type == QUERY_TYPE.SELECT;
    }

    public boolean isDelete() {
        return this.type == QUERY_TYPE.DELETE;
    }
    public boolean isInsert() {
        return this.type == QUERY_TYPE.INSERT;
    }

    public boolean isUpdate() {
        return this.type == QUERY_TYPE.UPDATE;
    }


    public Map<Integer, Condition<?>> getCondition(List<Condition<?>> conditions) {
        return this.names.entrySet().stream().collect(Collectors.toMap(  Map.Entry::getKey, entry-> conditions.stream()
            .filter(condition -> condition.getName().equals(entry.getValue())).findFirst().orElse(null)));
    }

    public String getSQL() {
        // TODO : Listが渡ったときの返し方。
        return this.sql.replaceAll("\\$\\{.*\\}", "?");
    }

}
