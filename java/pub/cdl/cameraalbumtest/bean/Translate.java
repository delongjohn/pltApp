package pub.cdl.cameraalbumtest.bean;

import java.util.List;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/30 0:38
 * 4
 */
public class Translate {
    private String from;
    private String to;
    private List<TrResult> trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<TrResult> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TrResult> trans_result) {
        this.trans_result = trans_result;
    }
}
