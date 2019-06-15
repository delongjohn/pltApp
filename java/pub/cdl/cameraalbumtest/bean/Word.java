package pub.cdl.cameraalbumtest.bean;

import pub.cdl.cameraalbumtest.R;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/25 1:52
 * 4
 */
public class Word {
    public static final int IMG = R.drawable.timg;
    private String word;
    private int count;
    private String translate;
    private Tag tag;
    private static final String space = " ";

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public int getCount() {
        return count;
    }

    public void setCount() {
        this.count++;
    }

    public Tag getTag() {
        return tag;
    }

    public Word() {
        translate = space;
        count = 1;
        tag = new Tag();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", translate='" + translate + '\'' +
                '}';
    }

    public class Tag {
        boolean four;
        boolean six;
        boolean high;
        boolean normal;

        Tag() {
            four = false;
            six = false;
            high = false;
            normal = true;
        }

        public void setFour() {
            this.four = true;
            normal = false;
        }

        public void setSix() {
            this.six = true;
            normal = false;
        }

        public void setHigh() {
            this.high = true;
            normal = false;
        }

        public boolean isNormal() {
            return normal;
        }
    }
}
