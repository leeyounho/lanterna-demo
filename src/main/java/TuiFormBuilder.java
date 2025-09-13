import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TuiFormBuilder implements Closeable {

    private final Terminal terminal;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    // ... (내부 클래스 및 생성자, 빌더 메소드는 이전과 동일)
    private static class FormItem {
        final String key;
        final Question action;
        FormItem(String key, Question action) { this.key = key; this.action = action; }
    }

    @FunctionalInterface
    private interface Question {
        Optional<Object> ask(WindowBasedTextGUI gui);
    }

    private final List<FormItem> items = new ArrayList<>();

    public TuiFormBuilder() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));

        this.textGUI.setTheme(new SimpleTheme(TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK));

    }

    public TuiFormBuilder addTextInput(String key, String title, String description) {
        items.add(new FormItem(key, gui -> showTextInputDialog(gui, title, description)));
        return this;
    }

    public TuiFormBuilder addCheckboxList(String key, String title, String description, String... options) {
        items.add(new FormItem(key, gui -> showCheckboxDialog(gui, title, description, Arrays.asList(options))));
        return this;
    }

    public TuiFormBuilder addRadioList(String key, String title, String description, String... options) {
        items.add(new FormItem(key, gui -> showRadioDialog(gui, title, description, Arrays.asList(options))));
        return this;
    }

    public TuiFormBuilder addConfirmation(String key, String title, String text) {
        items.add(new FormItem(key, gui -> showConfirmDialog(gui, title, text)));
        return this;
    }

    public TuiForm run() throws IOException {
        screen.startScreen();
        Map<String, Object> results = new LinkedHashMap<>();
        for (FormItem item : items) {
            Optional<Object> result = item.action.ask(textGUI);
            if (!result.isPresent()) {
                return null;
            }
            results.put(item.key, result.get());
        }
        return new TuiForm(results);
    }

    @Override
    public void close() throws IOException {
        if (this.screen != null) {
            this.screen.stopScreen();
        }
    }

    // --- Private Helper Methods for showing dialogs (수정됨) ---

    private Optional<Object> showTextInputDialog(WindowBasedTextGUI gui, String title, String description) {
        // 1. 람다에서 사용할 변수들을 먼저 선언합니다.
        AtomicBoolean confirmed = new AtomicBoolean(false);
        BasicWindow window = new BasicWindow();

        Panel contentPanel = new Panel(new GridLayout(2));
        contentPanel.addComponent(new Label(description));
        TextBox textBox = new TextBox();
        contentPanel.addComponent(textBox);

        Panel buttonPanel = new Panel(new GridLayout(2));
        // 2. 이제 `confirmed`와 `window` 변수를 안전하게 사용할 수 있습니다.
        Button okButton = new Button("OK", () -> { confirmed.set(true); window.close(); });
        buttonPanel.addComponent(okButton);
        Button cancelButton = new Button("Cancel", window::close);
        buttonPanel.addComponent(cancelButton);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        contentPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        contentPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        contentPanel.addComponent(buttonPanel);

        window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS));
        Border border = Borders.singleLine(title);
        border.setComponent(contentPanel);
        window.setComponent(border);
        window.setFocusedInteractable(textBox);
        gui.addWindowAndWait(window);

        return confirmed.get() ? Optional.of(textBox.getText()) : Optional.empty();
    }

    private Optional<Object> showCheckboxDialog(WindowBasedTextGUI gui, String title, String description, List<String> options) {
        AtomicBoolean confirmed = new AtomicBoolean(false);
        BasicWindow window = new BasicWindow();

        Panel contentPanel = new Panel(new GridLayout(1));
        contentPanel.addComponent(new Label(description));
        contentPanel.addComponent(new EmptySpace(new TerminalSize(0,1)));

        List<CheckBox> checkBoxes = options.stream().map(CheckBox::new).peek(contentPanel::addComponent).collect(Collectors.toList());

        contentPanel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        Panel buttonPanel = new Panel(new GridLayout(2));
        Button okButton = new Button("OK", () -> { confirmed.set(true); window.close(); });
        buttonPanel.addComponent(okButton);
        Button cancelButton = new Button("Cancel", window::close);
        buttonPanel.addComponent(cancelButton);
        contentPanel.addComponent(buttonPanel);

        window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS));
        Border border = Borders.singleLine(title);
        border.setComponent(contentPanel);
        window.setComponent(border);
        gui.addWindowAndWait(window);

        if (confirmed.get()) {
            return Optional.of(checkBoxes.stream().filter(CheckBox::isChecked).map(CheckBox::getLabel).collect(Collectors.toList()));
        }
        return Optional.empty();
    }

    private Optional<Object> showRadioDialog(WindowBasedTextGUI gui, String title, String description, List<String> options) {
        AtomicBoolean confirmed = new AtomicBoolean(false);
        BasicWindow window = new BasicWindow();

        Panel contentPanel = new Panel(new GridLayout(1));
        contentPanel.addComponent(new Label(description));
        contentPanel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        RadioBoxList<String> radioBoxList = new RadioBoxList<>();
        options.forEach(radioBoxList::addItem);
        if (!options.isEmpty()) radioBoxList.setSelectedIndex(0);
        contentPanel.addComponent(radioBoxList);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        Panel buttonPanel = new Panel(new GridLayout(2));
        Button okButton = new Button("OK", () -> { confirmed.set(true); window.close(); });
        buttonPanel.addComponent(okButton);
        Button cancelButton = new Button("Cancel", window::close);
        buttonPanel.addComponent(cancelButton);
        contentPanel.addComponent(buttonPanel);

        window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS));
        Border border = Borders.singleLine(title);
        border.setComponent(contentPanel);
        window.setComponent(border);
        gui.addWindowAndWait(window);

        return confirmed.get() ? Optional.ofNullable(radioBoxList.getCheckedItem()) : Optional.empty();
    }

    private Optional<Object> showConfirmDialog(WindowBasedTextGUI gui, String title, String text) {
        AtomicBoolean confirmed = new AtomicBoolean(false);
        BasicWindow window = new BasicWindow();

        Panel contentPanel = new Panel(new GridLayout(1));
        contentPanel.addComponent(new Label(text).setLayoutData(GridLayout.createHorizontallyFilledLayoutData()));

        contentPanel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        Panel buttonPanel = new Panel(new GridLayout(2));
        Button yesButton = new Button("Yes", () -> { confirmed.set(true); window.close(); });
        buttonPanel.addComponent(yesButton);
        Button noButton = new Button("No", window::close);
        buttonPanel.addComponent(noButton);
        contentPanel.addComponent(buttonPanel);

        window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS));
        Border border = Borders.singleLine(title);
        border.setComponent(contentPanel);
        window.setComponent(border);
        gui.addWindowAndWait(window);

        return confirmed.get() ? Optional.of(true) : Optional.empty();
    }
}