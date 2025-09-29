package net.weichware.jbdao.ui.dialog;

public class ErrorDialog extends BaseDialog {

    public ErrorDialog(String title, String text) {
        super(title);
        addContent(createDivFromText(text));
        addCancelButton("OK");
    }


}
