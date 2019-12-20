// Generated from /home/moss/repos/sql_tool/parser/Definition.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DefinitionLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, OPTION=3, DATA_TYPE=4, NAME=5, WS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "OPTION", "DATA_TYPE", "NAME", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "OPTION", "DATA_TYPE", "NAME", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public DefinitionLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Definition.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\bY\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\66\n\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\5\5L\n\5\3\6\6\6O\n\6\r\6\16\6P\3\7\6\7T\n\7\r\7\16\7U\3\7\3\7\2\2"+
		"\b\3\3\5\4\7\5\t\6\13\7\r\b\3\2\4\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\2"+
		"`\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\3\17\3\2\2\2\5\21\3\2\2\2\7\65\3\2\2\2\tK\3\2\2\2\13N\3\2\2\2\rS"+
		"\3\2\2\2\17\20\7}\2\2\20\4\3\2\2\2\21\22\7\177\2\2\22\6\3\2\2\2\23\24"+
		"\7r\2\2\24\25\7t\2\2\25\26\7k\2\2\26\27\7o\2\2\27\30\7c\2\2\30\31\7t\2"+
		"\2\31\66\7{\2\2\32\33\7c\2\2\33\34\7w\2\2\34\35\7v\2\2\35\36\7q\2\2\36"+
		"\37\7a\2\2\37 \7k\2\2 !\7p\2\2!\"\7e\2\2\"#\7t\2\2#$\7g\2\2$%\7o\2\2%"+
		"&\7g\2\2&\'\7p\2\2\'\66\7v\2\2()\7w\2\2)*\7p\2\2*+\7k\2\2+,\7s\2\2,-\7"+
		"w\2\2-\66\7g\2\2./\7h\2\2/\60\7q\2\2\60\61\7t\2\2\61\62\7g\2\2\62\63\7"+
		"k\2\2\63\64\7i\2\2\64\66\7p\2\2\65\23\3\2\2\2\65\32\3\2\2\2\65(\3\2\2"+
		"\2\65.\3\2\2\2\66\b\3\2\2\2\678\7k\2\289\7p\2\29L\7v\2\2:;\7d\2\2;<\7"+
		"q\2\2<=\7q\2\2=>\7n\2\2>?\7g\2\2?@\7c\2\2@L\7p\2\2AB\7u\2\2BC\7v\2\2C"+
		"D\7t\2\2DE\7k\2\2EF\7p\2\2FL\7i\2\2GH\7e\2\2HI\7j\2\2IJ\7c\2\2JL\7t\2"+
		"\2K\67\3\2\2\2K:\3\2\2\2KA\3\2\2\2KG\3\2\2\2L\n\3\2\2\2MO\t\2\2\2NM\3"+
		"\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\f\3\2\2\2RT\t\3\2\2SR\3\2\2\2TU"+
		"\3\2\2\2US\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\b\7\2\2X\16\3\2\2\2\7\2\65KP"+
		"U\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}