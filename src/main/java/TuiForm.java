import java.util.List;
import java.util.Map;

/**
 * TuiFormBuilder 실행 후의 결과물을 담는 클래스.
 * 각 질문의 키(key)를 사용하여 타입에 맞게 답변을 가져올 수 있습니다.
 */
public class TuiForm {
    private final Map<String, Object> results;

    public TuiForm(Map<String, Object> results) {
        this.results = results;
    }

    /**
     * 키(key)에 해당하는 답변을 String으로 가져옵니다.
     * @param key 질문을 추가할 때 지정한 키
     * @return 사용자의 답변 문자열
     */
    public String getString(String key) {
        return (String) results.get(key);
    }

    /**
     * 키(key)에 해당하는 답변을 List<String>으로 가져옵니다.
     * @param key 질문을 추가할 때 지정한 키
     * @return 사용자가 선택한 목록
     */
    @SuppressWarnings("unchecked")
    public List<String> getList(String key) {
        return (List<String>) results.get(key);
    }

    /**
     * 키(key)에 해당하는 답변을 boolean으로 가져옵니다.
     * @param key 질문을 추가할 때 지정한 키
     * @return 사용자의 Y/N 선택 결과
     */
    public boolean getBoolean(String key) {
        Object value = results.get(key);
        // 사용자가 No를 누르면 Optional.empty()가 되어 false를 반환해야 함
        if (value == null) return false;
        return value instanceof Boolean && (Boolean) value;
    }

    /**
     * 모든 결과가 담긴 Map을 반환합니다.
     * @return 결과 Map
     */
    public Map<String, Object> getAll() {
        return results;
    }
}