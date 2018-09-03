package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ansoft.speedup.R;

public class InequalityToggleButton extends LinearLayout {
    private static final String[] inequalityNames;
    private int currentIndex;
    private String currentInequality;
    private Button inequalityButton;
    private TextView textView;

    /* renamed from: com.ansoft.setthecpu.widget.InequalityToggleButton.1 */
    class C01401 implements OnClickListener {
        C01401() {
        }

        public void onClick(View v) {
            if (InequalityToggleButton.this.currentIndex >= InequalityToggleButton.inequalityNames.length - 1) {
                InequalityToggleButton.this.currentIndex = 0;
            } else {
                InequalityToggleButton.this.currentIndex = InequalityToggleButton.this.currentIndex + 1;
            }
            InequalityToggleButton.this.currentInequality = InequalityToggleButton.inequalityNames[InequalityToggleButton.this.currentIndex];
            InequalityToggleButton.this.inequalityButton.setText(InequalityToggleButton.this.currentInequality);
        }
    }

    static {
        inequalityNames = new String[]{"=", "<=", ">=", "<", ">", "!="};
    }

    public InequalityToggleButton(Context context) {
        super(context);
        this.currentInequality = "=";
        this.currentIndex = 0;
        setup(context);
    }

    public InequalityToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentInequality = "=";
        this.currentIndex = 0;
        setup(context);
    }

    private void setup(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTextColor(-1);
        this.inequalityButton = new Button(context);
        setLayoutParams(new LayoutParams(-2, -2));
        this.inequalityButton.setBackgroundResource(R.drawable.bg_white);
        this.inequalityButton.setText(this.currentInequality);
        this.inequalityButton.setOnClickListener(new C01401());
        addView(this.textView);
        addView(this.inequalityButton);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public boolean setInequality(String inequality) {
        for (int i = 0; i < inequalityNames.length; i++) {
            if (inequality.equals(inequalityNames[i])) {
                this.currentInequality = inequality;
                this.inequalityButton.setText(inequality);
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
        return this.inequalityButton;
    }

    public TextView getTextView() {
        return this.textView;
    }
}
