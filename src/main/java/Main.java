import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        final Label lblOutput = new Label("");

        panel.addComponent(new Label("Num 1"));
        final TextBox txtNum1 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Num 2"));
        final TextBox txtNum2 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Num 1"));
        final TextBox txtNum3 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Num 2"));
        final TextBox txtNum4 = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Num 2"));
        panel.addComponent(new CheckBox("체크 박스 1 asdklasjdfkjlsdfhglsdhfljsdf"));
        panel.addComponent(new Label("Num 2"));
        panel.addComponent(new CheckBox("체크 박스 2"));

        panel.addComponent(new Label("Operation"));
        final ComboBox<String> operations = new ComboBox<>();
        operations.addItem("Add");
        operations.addItem("Subtract");
        panel.addComponent(operations);


        new Button("Submit", () -> {
            int num1 = Integer.parseInt(txtNum1.getText());
            int num2 = Integer.parseInt(txtNum2.getText());
            if(operations.getSelectedIndex() == 0) {
                lblOutput.setText(Integer.toString(num1 + num2));
            } else if(operations.getSelectedIndex() == 1) {
                lblOutput.setText(Integer.toString(num1 - num2));
            }
        }).addTo(panel);

        new Button("Cancel", () -> {
            return;
        }).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(lblOutput);

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        Theme theme = SimpleTheme.makeTheme(true, TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK);
        gui.setTheme(theme);


        gui.addWindowAndWait(window);
    }
}
