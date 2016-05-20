package mata.macrokey.language;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private ArrayList<TokenData> tokenDatas;

    private String str;

    private Token lastToken;
    private boolean pushBack;

    public Tokenizer(String str) {
        this.tokenDatas = new ArrayList<TokenData>();
        this.str = str;

        tokenDatas.add(new TokenData(Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*)"), TokenType.IDENTIFIER));
        tokenDatas.add(new TokenData(Pattern.compile("^((-)?[0-9]+)"), TokenType.INTEGER_LITERAL));
        tokenDatas.add(new TokenData(Pattern.compile("^(\".*\")"), TokenType.STRING_LITERAL));

        for (String t : new String[] { "\\(", "\\)", "\\;" }) {
            tokenDatas.add(new TokenData(Pattern.compile("^(" + t + ")"), TokenType.TOKEN));
        }
    }

    public Token nextToken() {
        str = str.trim();

        if (pushBack) {
            pushBack = false;
            return lastToken;
        }

        if (str.isEmpty()) {
            return (lastToken = new Token("", TokenType.EMPTY));
        }

        for (TokenData data : tokenDatas) {
            Matcher matcher = data.getPattern().matcher(str);

            if (matcher.find()) {
                String token = matcher.group().trim();

                str = matcher.replaceFirst("");

                if (data.getType() == TokenType.STRING_LITERAL) {
                    return (lastToken = new Token(token.substring(1, token.length() - 1), TokenType.STRING_LITERAL));
                }

                else {
                    return (lastToken = new Token(token, data.getType()));
                }
            }
        }

        return new Token(str.replaceAll("\n",""), TokenType.ERROR);
    }

    public boolean hasNextToken() {
        return !str.isEmpty();
    }

    public void pushBack() {
        if (lastToken != null) {
            this.pushBack = true;
        }
    }
}