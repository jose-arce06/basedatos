import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UiComponents {
    // Borde redondeado
    public static class RoundedBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,60));
            g2.drawRoundRect(x, y, w - 1, h - 1, UiConstants.ARC, UiConstants.ARC);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 15, 10, 15);
        }
    }

    // Botón redondeado
    public static class RoundedButton extends JButton {
        public RoundedButton(String text, Color bg) {
            super(text);
            setFont(UiConstants.FUENTE_BOLD);
            setForeground(UiConstants.COLOR_BLANCO);
            setBackground(bg);
            setBorder(new RoundedBorder());
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = getBackground();
            if (getModel().isPressed()) {
                base = base.darker();
            } else if (getModel().isRollover()) {
                base = base.brighter();
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), UiConstants.ARC, UiConstants.ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setBackground(UiConstants.COLOR_BLANCO);
        campo.setForeground(Color.BLACK);
        campo.setFont(UiConstants.FUENTE_BASE);
        return campo;
    }

    public static JPasswordField crearCampoPassword() {
        JPasswordField campo = new JPasswordField();
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setBackground(UiConstants.COLOR_BLANCO);
        campo.setForeground(Color.BLACK);
        campo.setFont(UiConstants.FUENTE_BASE);
        return campo;
    }

    public static JTextField crearCampoTextoLogin() {
        JTextField campo = new JTextField();
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        campo.setBackground(UiConstants.COLOR_BLANCO);
        campo.setForeground(Color.BLACK);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return campo;
    }

    public static JPasswordField crearCampoPasswordLogin() {
        JPasswordField campo = new JPasswordField();
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        campo.setBackground(UiConstants.COLOR_BLANCO);
        campo.setForeground(Color.BLACK);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setEchoChar('•');
        return campo;
    }

    public static JButton crearBoton(String texto, Color color) {
        JButton boton = new RoundedButton(texto, color);
        boton.setPreferredSize(new Dimension(150, 45));
        return boton;
    }

    public static JComboBox<String> crearComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        combo.setBackground(UiConstants.COLOR_BLANCO);
        combo.setForeground(Color.BLACK);
        combo.setFont(UiConstants.FUENTE_BASE);
        return combo;
    }
}
