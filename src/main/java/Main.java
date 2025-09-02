import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // try-with-resources로 TuiFormBuilder가 자동으로 close 되도록 합니다.
        try (TuiFormBuilder builder = new TuiFormBuilder()) {

            // 빌더를 사용하여 질문의 순서와 내용을 정의하는 방식은 완전히 동일합니다.
            TuiForm form = builder
                    .addTextInput("username", "사용자 정보", "이름을 입력하세요:")
                    .addRadioList("skill", "기술 수준", "Java 숙련도를 선택하세요:",
                            "Beginner", "Intermediate", "Advanced")
                    .addCheckboxList("interests", "관심 분야", "관심사를 모두 선택하세요:",
                            "Cloud", "Database", "AI/ML", "Front-end")
                    .addConfirmation("confirm", "프로필 생성", "입력한 내용으로 프로필을 생성하시겠습니까?")
                    .run(); // run()을 호출하면 정의된 순서대로 대화창이 나타납니다.

            // 사용자가 '취소'했거나 창을 그냥 닫았을 경우 form이 null일 수 있습니다.
            if (form == null) {
                System.out.println("TUI 입력이 취소되었습니다.");
                return;
            }

            // TUI 실행이 끝나면, form 객체에서 결과를 꺼내 처리합니다.
            System.out.println("\n--- TUI 입력 결과 ---");
            System.out.println("사용자 이름: " + form.getString("username"));
            System.out.println("숙련도: " + form.getString("skill"));
            System.out.println("관심사: " + form.getList("interests"));
            System.out.println("생성 동의: " + form.getBoolean("confirm"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}