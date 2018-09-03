package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChargeToggleButton extends LinearLayout {
    private static final String[] names;
    private Button button;
    private int currentIndex;
    private String currentInequality;
    private TextView textView;

    /* renamed from: com.ansoft.setthecpu.widget.ChargeToggleButton.1 */
    class C01391 implements OnClickListener {
        C01391() {
        }

        public void onClick(View v) {
            if (ChargeToggleButton.this.currentIndex >= ChargeToggleButton.names.length - 1) {
                ChargeToggleButton.this.currentIndex = 0;
            } else {
                ChargeToggleButton.this.currentIndex = ChargeToggleButton.this.currentIndex + 1;
            }
            ChargeToggleButton.this.currentInequality = ChargeToggleButton.names[ChargeToggleButton.this.currentIndex];
            ChargeToggleButton.this.button.setText(ChargeToggleButton.this.currentInequality);
        }
    }

    static {
        names = new String[]{"Any", "AC", "USB", "wireless"};
    }

    public ChargeToggleButton(Context context) {
        super(context);
        this.currentInequality = "Any";
        this.currentIndex = 0;
        setup(context);
    }

    public ChargeToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentInequality = "Any";
        this.currentIndex = 0;
        setup(context);
    }

    public ChargeToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.currentInequality = "Any";
        this.currentIndex = 0;
        setup(context);
    }

    private void setup(Context context) {
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTextColor(-1);
        this.button = new Button(context);
        setLayoutParams(new LayoutParams(-2, -2));
        this.button.setText(this.currentInequality);
        this.button.setOnClickListener(new C01391());
        addView(this.textView);
        addView(this.button);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public boolean setInequality(String inequality) {
        for (int i = 0; i < names.length; i++) {
            if (inequality.equals(names[i])) {
                this.currentInequality = inequality;
                this.button.setText(inequality);
                this.currentIndex = i;
                return true;
            }
        }
        return false;
    }

    public String getInequality() {
        return this.currentInequality;
    }

    public CharSequence getText() {
        return this.textView.getText();
    }

    public Button getButton() {
        return this.button;
    }

    public TextView getTextView() {
        return this.textView;
    }
}
