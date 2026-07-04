package cn.ignai.hackathon.integration.feishu;

public class FeishuSyncClient {
    public String syncRecord(String targetType, long targetId) {
        // TODO: implement Feishu Base / Drive API. MVP can export CSV or JSON first.
        return "queued:" + targetType + ":" + targetId;
    }
}
