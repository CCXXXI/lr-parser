import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Java_LRParserAnalysis {

  public static void main(String[] args) throws IOException {
    final LRParser parser =
        new LRParser(new LineNumberReader(new InputStreamReader(System.in)));
    final String result = parser.parse();
    final String error = parser.errorMessages.toString();
    System.out.println(error + result);
  }

  /**
   * Why is this not in the standard library?
   */
  static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
  }

  @SuppressWarnings({"SpellCheckingInspection",
                     "ArraysAsListWithZeroOrOneArgument",
                     "PatternVariableCanBeUsed"})
  static class LRParser {

    private static final Map<String, Map<Integer, Action>> transitions =
        new HashMap<>();
    private static final Map<String, Map<Integer, Integer>> gotoTable =
        new HashMap<>();

    // The stupid platform only supports Java 8,
    // so we have to use this stupid way to init the set and the map.

    // region init

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("boolexpr", map);

      map.put(15, 19);
      map.put(16, 26);
      map.put(105, 114);
      map.put(106, 115);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("arithexpr", map);

      map.put(15, 20);
      map.put(16, 20);
      map.put(17, 27);
      map.put(25, 47);
      map.put(32, 61);
      map.put(35, 63);
      map.put(52, 75);
      map.put(105, 20);
      map.put(106, 20);
      map.put(107, 116);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("multexpr", map);

      map.put(15, 21);
      map.put(16, 21);
      map.put(17, 28);
      map.put(25, 48);
      map.put(32, 48);
      map.put(35, 48);
      map.put(42, 64);
      map.put(43, 65);
      map.put(52, 48);
      map.put(56, 77);
      map.put(57, 78);
      map.put(70, 95);
      map.put(71, 96);
      map.put(105, 21);
      map.put(106, 21);
      map.put(107, 28);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("simpleexpr", map);

      map.put(15, 22);
      map.put(16, 22);
      map.put(17, 29);
      map.put(25, 49);
      map.put(32, 49);
      map.put(35, 49);
      map.put(42, 22);
      map.put(43, 22);
      map.put(45, 66);
      map.put(46, 67);
      map.put(52, 49);
      map.put(56, 29);
      map.put(57, 29);
      map.put(59, 79);
      map.put(60, 80);
      map.put(70, 49);
      map.put(71, 49);
      map.put(73, 97);
      map.put(74, 98);
      map.put(105, 22);
      map.put(106, 22);
      map.put(107, 29);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("boolop", map);

      map.put(20, 35);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("arithexprprime", map);

      map.put(21, 41);
      map.put(28, 55);
      map.put(48, 69);
      map.put(64, 91);
      map.put(65, 92);
      map.put(77, 100);
      map.put(78, 101);
      map.put(95, 109);
      map.put(96, 110);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("multexprprime", map);

      map.put(22, 44);
      map.put(29, 58);
      map.put(49, 72);
      map.put(66, 93);
      map.put(67, 94);
      map.put(79, 102);
      map.put(80, 103);
      map.put(97, 111);
      map.put(98, 112);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("compoundstmt", map);

      map.put(0, 1);
      map.put(2, 8);
      map.put(4, 8);
      map.put(12, 8);
      map.put(53, 8);
      map.put(62, 86);
      map.put(90, 8);
      map.put(104, 8);
      map.put(119, 86);
      map.put(121, 86);
      map.put(124, 86);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("stmt", map);

      map.put(2, 4);
      map.put(4, 4);
      map.put(12, 4);
      map.put(53, 76);
      map.put(62, 82);
      map.put(90, 4);
      map.put(104, 113);
      map.put(119, 122);
      map.put(121, 123);
      map.put(124, 125);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("stmts", map);

      map.put(2, 3);
      map.put(4, 14);
      map.put(12, 18);
      map.put(90, 108);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("ifstmt", map);

      map.put(2, 5);
      map.put(4, 5);
      map.put(12, 5);
      map.put(53, 5);
      map.put(62, 83);
      map.put(90, 5);
      map.put(104, 5);
      map.put(119, 83);
      map.put(121, 83);
      map.put(124, 83);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("whilestmt", map);

      map.put(2, 6);
      map.put(4, 6);
      map.put(12, 6);
      map.put(53, 6);
      map.put(62, 84);
      map.put(90, 6);
      map.put(104, 6);
      map.put(119, 84);
      map.put(121, 84);
      map.put(124, 84);
    }

    static {
      Map<Integer, Integer> map = new HashMap<>();
      gotoTable.put("assgstmt", map);

      map.put(2, 7);
      map.put(4, 7);
      map.put(12, 7);
      map.put(53, 7);
      map.put(62, 85);
      map.put(90, 7);
      map.put(104, 7);
      map.put(119, 85);
      map.put(121, 85);
      map.put(124, 85);
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("{", map);

      map.put(0, new Shift(2));
      map.put(2, new Shift(12));
      map.put(4, new Shift(12));
      map.put(5, new Reduce(1));
      map.put(6, new Reduce(2));
      map.put(7, new Reduce(3));
      map.put(8, new Reduce(4));
      map.put(12, new Shift(12));
      map.put(33, new Reduce(5));
      map.put(53, new Shift(12));
      map.put(54, new Reduce(10));
      map.put(62, new Shift(90));
      map.put(76, new Reduce(9));
      map.put(90, new Shift(12));
      map.put(104, new Shift(12));
      map.put(113, new Reduce(8));
      map.put(119, new Shift(90));
      map.put(121, new Shift(90));
      map.put(124, new Shift(90));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("$", map);

      map.put(1, new Acc());
      map.put(13, new Reduce(5));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("}", map);

      map.put(2, new Reduce(7));
      map.put(3, new Shift(13));
      map.put(4, new Reduce(7));
      map.put(5, new Reduce(1));
      map.put(6, new Reduce(2));
      map.put(7, new Reduce(3));
      map.put(8, new Reduce(4));
      map.put(12, new Reduce(7));
      map.put(14, new Reduce(6));
      map.put(18, new Shift(33));
      map.put(33, new Reduce(5));
      map.put(54, new Reduce(10));
      map.put(76, new Reduce(9));
      map.put(90, new Reduce(7));
      map.put(108, new Shift(117));
      map.put(113, new Reduce(8));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("if", map);

      map.put(2, new Shift(9));
      map.put(4, new Shift(9));
      map.put(5, new Reduce(1));
      map.put(6, new Reduce(2));
      map.put(7, new Reduce(3));
      map.put(8, new Reduce(4));
      map.put(12, new Shift(9));
      map.put(33, new Reduce(5));
      map.put(53, new Shift(9));
      map.put(54, new Reduce(10));
      map.put(62, new Shift(87));
      map.put(76, new Reduce(9));
      map.put(90, new Shift(9));
      map.put(104, new Shift(9));
      map.put(113, new Reduce(8));
      map.put(119, new Shift(87));
      map.put(121, new Shift(87));
      map.put(124, new Shift(87));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("while", map);

      map.put(2, new Shift(10));
      map.put(4, new Shift(10));
      map.put(5, new Reduce(1));
      map.put(6, new Reduce(2));
      map.put(7, new Reduce(3));
      map.put(8, new Reduce(4));
      map.put(12, new Shift(10));
      map.put(33, new Reduce(5));
      map.put(53, new Shift(10));
      map.put(54, new Reduce(10));
      map.put(62, new Shift(88));
      map.put(76, new Reduce(9));
      map.put(90, new Shift(10));
      map.put(104, new Shift(10));
      map.put(113, new Reduce(8));
      map.put(119, new Shift(88));
      map.put(121, new Shift(88));
      map.put(124, new Shift(88));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("ID", map);

      map.put(2, new Shift(11));
      map.put(4, new Shift(11));
      map.put(5, new Reduce(1));
      map.put(6, new Reduce(2));
      map.put(7, new Reduce(3));
      map.put(8, new Reduce(4));
      map.put(12, new Shift(11));
      map.put(15, new Shift(23));
      map.put(16, new Shift(23));
      map.put(17, new Shift(30));
      map.put(25, new Shift(50));
      map.put(32, new Shift(50));
      map.put(33, new Reduce(5));
      map.put(35, new Shift(50));
      map.put(36, new Reduce(12));
      map.put(37, new Reduce(13));
      map.put(38, new Reduce(14));
      map.put(39, new Reduce(15));
      map.put(40, new Reduce(16));
      map.put(42, new Shift(23));
      map.put(43, new Shift(23));
      map.put(45, new Shift(23));
      map.put(46, new Shift(23));
      map.put(52, new Shift(50));
      map.put(53, new Shift(11));
      map.put(54, new Reduce(10));
      map.put(56, new Shift(30));
      map.put(57, new Shift(30));
      map.put(59, new Shift(30));
      map.put(60, new Shift(30));
      map.put(62, new Shift(89));
      map.put(70, new Shift(50));
      map.put(71, new Shift(50));
      map.put(73, new Shift(50));
      map.put(74, new Shift(50));
      map.put(76, new Reduce(9));
      map.put(90, new Shift(11));
      map.put(104, new Shift(11));
      map.put(105, new Shift(23));
      map.put(106, new Shift(23));
      map.put(107, new Shift(30));
      map.put(113, new Reduce(8));
      map.put(119, new Shift(89));
      map.put(121, new Shift(89));
      map.put(124, new Shift(89));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("(", map);

      map.put(9, new Shift(15));
      map.put(10, new Shift(16));
      map.put(15, new Shift(25));
      map.put(16, new Shift(25));
      map.put(17, new Shift(32));
      map.put(25, new Shift(52));
      map.put(32, new Shift(52));
      map.put(35, new Shift(52));
      map.put(36, new Reduce(12));
      map.put(37, new Reduce(13));
      map.put(38, new Reduce(14));
      map.put(39, new Reduce(15));
      map.put(40, new Reduce(16));
      map.put(42, new Shift(25));
      map.put(43, new Shift(25));
      map.put(45, new Shift(25));
      map.put(46, new Shift(25));
      map.put(52, new Shift(52));
      map.put(56, new Shift(32));
      map.put(57, new Shift(32));
      map.put(59, new Shift(32));
      map.put(60, new Shift(32));
      map.put(70, new Shift(52));
      map.put(71, new Shift(52));
      map.put(73, new Shift(52));
      map.put(74, new Shift(52));
      map.put(87, new Shift(105));
      map.put(88, new Shift(106));
      map.put(105, new Shift(25));
      map.put(106, new Shift(25));
      map.put(107, new Shift(32));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("=", map);

      map.put(11, new Shift(17));
      map.put(89, new Shift(107));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("NUM", map);

      map.put(15, new Shift(24));
      map.put(16, new Shift(24));
      map.put(17, new Shift(31));
      map.put(25, new Shift(51));
      map.put(32, new Shift(51));
      map.put(35, new Shift(51));
      map.put(36, new Reduce(12));
      map.put(37, new Reduce(13));
      map.put(38, new Reduce(14));
      map.put(39, new Reduce(15));
      map.put(40, new Reduce(16));
      map.put(42, new Shift(24));
      map.put(43, new Shift(24));
      map.put(45, new Shift(24));
      map.put(46, new Shift(24));
      map.put(52, new Shift(51));
      map.put(56, new Shift(31));
      map.put(57, new Shift(31));
      map.put(59, new Shift(31));
      map.put(60, new Shift(31));
      map.put(70, new Shift(51));
      map.put(71, new Shift(51));
      map.put(73, new Shift(51));
      map.put(74, new Shift(51));
      map.put(105, new Shift(24));
      map.put(106, new Shift(24));
      map.put(107, new Shift(31));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put(")", map);

      map.put(19, new Shift(34));
      map.put(26, new Shift(53));
      map.put(47, new Shift(68));
      map.put(48, new Reduce(20));
      map.put(49, new Reduce(24));
      map.put(50, new Reduce(25));
      map.put(51, new Reduce(26));
      map.put(61, new Shift(81));
      map.put(63, new Reduce(11));
      map.put(69, new Reduce(17));
      map.put(72, new Reduce(21));
      map.put(75, new Shift(99));
      map.put(95, new Reduce(20));
      map.put(96, new Reduce(20));
      map.put(97, new Reduce(24));
      map.put(98, new Reduce(24));
      map.put(99, new Reduce(27));
      map.put(109, new Reduce(18));
      map.put(110, new Reduce(19));
      map.put(111, new Reduce(22));
      map.put(112, new Reduce(23));
      map.put(114, new Shift(118));
      map.put(115, new Shift(119));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("<", map);

      map.put(20, new Shift(36));
      map.put(21, new Reduce(20));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(41, new Reduce(17));
      map.put(44, new Reduce(21));
      map.put(64, new Reduce(20));
      map.put(65, new Reduce(20));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(91, new Reduce(18));
      map.put(92, new Reduce(19));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put(">", map);

      map.put(20, new Shift(37));
      map.put(21, new Reduce(20));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(41, new Reduce(17));
      map.put(44, new Reduce(21));
      map.put(64, new Reduce(20));
      map.put(65, new Reduce(20));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(91, new Reduce(18));
      map.put(92, new Reduce(19));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("<=", map);

      map.put(20, new Shift(38));
      map.put(21, new Reduce(20));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(41, new Reduce(17));
      map.put(44, new Reduce(21));
      map.put(64, new Reduce(20));
      map.put(65, new Reduce(20));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(91, new Reduce(18));
      map.put(92, new Reduce(19));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put(">=", map);

      map.put(20, new Shift(39));
      map.put(21, new Reduce(20));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(41, new Reduce(17));
      map.put(44, new Reduce(21));
      map.put(64, new Reduce(20));
      map.put(65, new Reduce(20));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(91, new Reduce(18));
      map.put(92, new Reduce(19));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("==", map);

      map.put(20, new Shift(40));
      map.put(21, new Reduce(20));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(41, new Reduce(17));
      map.put(44, new Reduce(21));
      map.put(64, new Reduce(20));
      map.put(65, new Reduce(20));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(91, new Reduce(18));
      map.put(92, new Reduce(19));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("+", map);

      map.put(21, new Shift(42));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(28, new Shift(56));
      map.put(29, new Reduce(24));
      map.put(30, new Reduce(25));
      map.put(31, new Reduce(26));
      map.put(44, new Reduce(21));
      map.put(48, new Shift(70));
      map.put(49, new Reduce(24));
      map.put(50, new Reduce(25));
      map.put(51, new Reduce(26));
      map.put(58, new Reduce(21));
      map.put(64, new Shift(42));
      map.put(65, new Shift(42));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(72, new Reduce(21));
      map.put(77, new Shift(56));
      map.put(78, new Shift(56));
      map.put(79, new Reduce(24));
      map.put(80, new Reduce(24));
      map.put(81, new Reduce(27));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
      map.put(95, new Shift(70));
      map.put(96, new Shift(70));
      map.put(97, new Reduce(24));
      map.put(98, new Reduce(24));
      map.put(99, new Reduce(27));
      map.put(102, new Reduce(22));
      map.put(103, new Reduce(23));
      map.put(111, new Reduce(22));
      map.put(112, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("-", map);

      map.put(21, new Shift(43));
      map.put(22, new Reduce(24));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(28, new Shift(57));
      map.put(29, new Reduce(24));
      map.put(30, new Reduce(25));
      map.put(31, new Reduce(26));
      map.put(44, new Reduce(21));
      map.put(48, new Shift(71));
      map.put(49, new Reduce(24));
      map.put(50, new Reduce(25));
      map.put(51, new Reduce(26));
      map.put(58, new Reduce(21));
      map.put(64, new Shift(43));
      map.put(65, new Shift(43));
      map.put(66, new Reduce(24));
      map.put(67, new Reduce(24));
      map.put(68, new Reduce(27));
      map.put(72, new Reduce(21));
      map.put(77, new Shift(57));
      map.put(78, new Shift(57));
      map.put(79, new Reduce(24));
      map.put(80, new Reduce(24));
      map.put(81, new Reduce(27));
      map.put(93, new Reduce(22));
      map.put(94, new Reduce(23));
      map.put(95, new Shift(71));
      map.put(96, new Shift(71));
      map.put(97, new Reduce(24));
      map.put(98, new Reduce(24));
      map.put(99, new Reduce(27));
      map.put(102, new Reduce(22));
      map.put(103, new Reduce(23));
      map.put(111, new Reduce(22));
      map.put(112, new Reduce(23));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("*", map);

      map.put(22, new Shift(45));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(29, new Shift(59));
      map.put(30, new Reduce(25));
      map.put(31, new Reduce(26));
      map.put(49, new Shift(73));
      map.put(50, new Reduce(25));
      map.put(51, new Reduce(26));
      map.put(66, new Shift(45));
      map.put(67, new Shift(45));
      map.put(68, new Reduce(27));
      map.put(79, new Shift(59));
      map.put(80, new Shift(59));
      map.put(81, new Reduce(27));
      map.put(97, new Shift(73));
      map.put(98, new Shift(73));
      map.put(99, new Reduce(27));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("/", map);

      map.put(22, new Shift(46));
      map.put(23, new Reduce(25));
      map.put(24, new Reduce(26));
      map.put(29, new Shift(60));
      map.put(30, new Reduce(25));
      map.put(31, new Reduce(26));
      map.put(49, new Shift(74));
      map.put(50, new Reduce(25));
      map.put(51, new Reduce(26));
      map.put(66, new Shift(46));
      map.put(67, new Shift(46));
      map.put(68, new Reduce(27));
      map.put(79, new Shift(60));
      map.put(80, new Shift(60));
      map.put(81, new Reduce(27));
      map.put(97, new Shift(74));
      map.put(98, new Shift(74));
      map.put(99, new Reduce(27));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put(";", map);

      map.put(27, new Shift(54));
      map.put(28, new Reduce(20));
      map.put(29, new Reduce(24));
      map.put(30, new Reduce(25));
      map.put(31, new Reduce(26));
      map.put(55, new Reduce(17));
      map.put(58, new Reduce(21));
      map.put(77, new Reduce(20));
      map.put(78, new Reduce(20));
      map.put(79, new Reduce(24));
      map.put(80, new Reduce(24));
      map.put(81, new Reduce(27));
      map.put(100, new Reduce(18));
      map.put(101, new Reduce(19));
      map.put(102, new Reduce(22));
      map.put(103, new Reduce(23));
      map.put(116, new Shift(120));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("then", map);

      map.put(34, new Shift(62));
      map.put(118, new Shift(121));
    }

    static {
      Map<Integer, Action> map = new HashMap<>();
      transitions.put("else", map);

      map.put(82, new Shift(104));
      map.put(83, new Reduce(1));
      map.put(84, new Reduce(2));
      map.put(85, new Reduce(3));
      map.put(86, new Reduce(4));
      map.put(117, new Reduce(5));
      map.put(120, new Reduce(10));
      map.put(122, new Reduce(9));
      map.put(123, new Shift(124));
      map.put(125, new Reduce(8));
    }

    public final StringBuilder errorMessages = new StringBuilder();
    final List<Grammar> grammars = Arrays.asList(
        new Grammar("program", Arrays.asList("compoundstmt")),
        new Grammar("stmt", Arrays.asList("ifstmt")),
        new Grammar("stmt", Arrays.asList("whilestmt")),
        new Grammar("stmt", Arrays.asList("assgstmt")),
        new Grammar("stmt", Arrays.asList("compoundstmt")),
        new Grammar("compoundstmt", Arrays.asList("{", "stmts", "}")),
        new Grammar("stmts", Arrays.asList("stmt", "stmts")),
        new Grammar("stmts", Arrays.asList()),
        new Grammar("ifstmt", Arrays.asList("if", "(", "boolexpr", ")", "then",
                                            "stmt", "else", "stmt")),
        new Grammar("whilestmt",
                    Arrays.asList("while", "(", "boolexpr", ")", "stmt")),
        new Grammar("assgstmt", Arrays.asList("ID", "=", "arithexpr", ";")),
        new Grammar("boolexpr",
                    Arrays.asList("arithexpr", "boolop", "arithexpr")),
        new Grammar("boolop", Arrays.asList("<")),
        new Grammar("boolop", Arrays.asList(">")),
        new Grammar("boolop", Arrays.asList("<=")),
        new Grammar("boolop", Arrays.asList(">=")),
        new Grammar("boolop", Arrays.asList("==")),
        new Grammar("arithexpr", Arrays.asList("multexpr", "arithexprprime")),
        new Grammar("arithexprprime",
                    Arrays.asList("+", "multexpr", "arithexprprime")),
        new Grammar("arithexprprime",
                    Arrays.asList("-", "multexpr", "arithexprprime")),
        new Grammar("arithexprprime", Arrays.asList()),
        new Grammar("multexpr", Arrays.asList("simpleexpr", "multexprprime")),
        new Grammar("multexprprime",
                    Arrays.asList("*", "simpleexpr", "multexprprime")),
        new Grammar("multexprprime",
                    Arrays.asList("/", "simpleexpr", "multexprprime")),
        new Grammar("multexprprime", Arrays.asList()),
        new Grammar("simpleexpr", Arrays.asList("ID")),
        new Grammar("simpleexpr", Arrays.asList("NUM")),
        new Grammar("simpleexpr", Arrays.asList("(", "arithexpr", ")")));
    private final LineNumberReader reader;
    private final Stack<Integer> stack = new Stack<>();
    private Scanner scanner;
    private String currentSymbol;
    private String patch;

    // endregion

    public LRParser(LineNumberReader reader) { this.reader = reader; }

    public String parse() throws IOException {
      nextSymbol();
      stack.push(0);

      final List<Reduce> result = new ArrayList<>();

      while (true) {
        int s = stack.peek();
        Action action =
            transitions.get(patch == null ? currentSymbol : patch).get(s);
        if (action instanceof Shift) {
          Shift shift = (Shift)action;
          stack.push(shift.state);
          if (patch == null) {
            nextSymbol();
          } else {
            patch = null;
          }
        } else if (action instanceof Reduce) {
          Reduce reduce = (Reduce)action;
          for (int i = 0; i < grammars.get(reduce.num).to.size(); i++) {
            stack.pop();
          }
          int t = stack.peek();
          stack.push(gotoTable.get((grammars.get(reduce.num)).from).get(t));
          result.add(reduce);
        } else if (action != null) {
          break; // done
        } else {
          Action guess = transitions.get(";").get(s);
          if (guess == null) {
            errorMessages.append("无法恢复的语法错误，第")
                .append(reader.getLineNumber())
                .append("行\n");
            break;
          } else {
            errorMessages.append("语法错误，第")
                .append(reader.getLineNumber() - 1)
                .append("行，缺少\";\"\n");
            patch = ";";
          }
        }
      }

      final StringBuilder sb = new StringBuilder();
      sb.append("program => \ncompoundstmt => \n");
      String cur = "compoundstmt";
      for (int i = result.size() - 1; i >= 0; i--) {
        Reduce reduce = result.get(i);
        cur = replaceLast(cur, grammars.get(reduce.num).from,
                          String.join(" ", grammars.get(reduce.num).to))
                  .replaceAll(" +", " ");
        sb.append(cur);
        if (i != 0) {
          sb.append(" => \n");
        }
      }
      return sb.toString();
    }

    /**
     * Reads the next symbol from the input.
     *
     * @see <a href="https://stackoverflow.com/a/1332316/13805358">Stack
     *     Overflow</a>
     */
    private void nextSymbol() throws IOException {
      if (scanner != null && scanner.hasNext()) {
        currentSymbol = scanner.next();
      } else {
        String line = reader.readLine();
        if (line != null) {
          scanner = new Scanner(line);
          nextSymbol();
        } else {
          currentSymbol = "$";
        }
      }
    }

    interface Action {}

    static class Reduce implements Action {

      int num;

      Reduce(int num) { this.num = num; }
    }

    static class Shift implements Action {

      int state;

      Shift(int state) { this.state = state; }
    }

    static class Acc implements Action {}

    static class Grammar {

      String from;
      List<String> to;

      Grammar(String from, List<String> to) {
        this.from = from;
        this.to = to;
      }
    }
  }
}
