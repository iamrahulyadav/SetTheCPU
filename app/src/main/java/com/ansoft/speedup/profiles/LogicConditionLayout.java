package com.ansoft.speedup.profiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.LogicAll;
import com.ansoft.speedup.profiles.condition.LogicAny;
import com.ansoft.speedup.profiles.condition.LogicNone;
import com.ansoft.speedup.util.ClickEffect;

import java.util.Iterator;

public class LogicConditionLayout extends LinearLayout {
    private static final int MAX_NEST_DEPTH = 7;
    private ImageView addButton;
    private OnConditionClickListener addListener;
    private AttributeSet attrs;
    private boolean clickable;
    private Condition condition;
    private Context context;
    private ImageView deleteButton;
    private LayoutInflater inf;
    private TextView title;

    /* renamed from: com.ansoft.setthecpu.profiles.LogicConditionLayout.1 */
    class C01201 implements OnClickListener {
        final /* synthetic */ View val$button;
        final /* synthetic */ Condition val$condition;

        C01201(Condition condition, View view) {
            this.val$condition = condition;
            this.val$button = view;
        }

        public void onClick(View v) {
            LogicConditionLayout.this.addListener.onConditionDelete(this.val$condition, this.val$button);
        }
    }

    /* renamed from: com.ansoft.setthecpu.profiles.LogicConditionLayout.2 */
    class C01212 implements OnClickListener {
        final /* synthetic */ View val$button;
        final /* synthetic */ Condition val$condition;

        C01212(Condition condition, View view) {
            this.val$condition = condition;
            this.val$button = view;
        }

        public void onClick(View v) {
            LogicConditionLayout.this.addListener.onConditionClick(this.val$condition, this.val$button);
        }
    }

    class C01223 implements OnClickListener {
        final /* synthetic */ Condition val$condition;

        C01223(Condition condition) {
            this.val$condition = condition;
        }

        public void onClick(View v) {
            LogicConditionLayout.this.addListener.onConditionAdd(this.val$condition, LogicConditionLayout.this.get());
        }
    }
    class C01234 implements OnClickListener {
        final /* synthetic */ Condition val$condition;

        C01234(Condition condition) {
            this.val$condition = condition;
        }

        public void onClick(View v) {
            this.val$condition.deleteSelf();
            LogicConditionLayout.this.addListener.onConditionDelete(this.val$condition, LogicConditionLayout.this.get());
        }
    }

    public interface OnConditionClickListener {
        void onConditionAdd(Condition condition, View view);

        void onConditionClick(Condition condition, View view);

        void onConditionDelete(Condition condition, View view);
    }

    public LogicConditionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.clickable = true;
        this.context = context;
        this.attrs = attrs;
        init();
    }

    @SuppressLint({"NewApi"})
    private void init() {
        setOrientation(VERTICAL);
        this.inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (VERSION.SDK_INT >= 11) {
            setLayerType(2, null);
        }
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public View addCondition(Condition condition, boolean root) {
        if (condition.getCategory() == 0) {
            LogicConditionLayout layout = (LogicConditionLayout) this.inf.inflate(R.layout.condition_predicate_layout, null, false).findViewById(R.id.predicate_layout);
            layout.setCondition(condition, root, this.addListener);
            condition.setParent(this.condition);
            addView(layout);
            return layout;
        } else if (condition.getCategory() != 1) {
            return null;
        } else {
            View button = this.inf.inflate(R.layout.condition_logic_header, null);
            ((TextView) button.findViewById(R.id.title)).setText(condition.getFormattedName(this.context));
            LinearLayout layout2 = (LinearLayout) button.findViewById(R.id.layoutss);
            layout2.setClickable(true);
            layout2.setBackgroundResource(R.drawable.list_selector_holo_dark);
            ImageView addButton = (ImageView) button.findViewById(R.id.addButton);
            ImageView deleteButton = (ImageView) button.findViewById(R.id.deleteButton);
            ClickEffect.Opacity(addButton);
            ClickEffect.Opacity(deleteButton);
            addButton.setEnabled(false);
            addButton.setVisibility(GONE);
            deleteButton.setOnClickListener(new C01201(condition, button));
            layout2.setOnClickListener(new C01212(condition, button));
            int i = 0;
            while (i < getChildCount() && !(getChildAt(i) instanceof LogicConditionLayout)) {
                i++;
            }
            condition.setParent(this.condition);
            addView(button, i);
            return button;
        }
    }

    public void setCondition(Condition condition, boolean root, OnConditionClickListener listener) {
        if (root) {
            removeAllViews();
        }
        View header = this.inf.inflate(R.layout.condition_logic_header, null);
        LinearLayout linLay=(LinearLayout)header.findViewById(R.id.linearLayout);

        this.title = (TextView) header.findViewById(R.id.title);
        this.addButton = (ImageView) header.findViewById(R.id.addButton);
        this.deleteButton = (ImageView) header.findViewById(R.id.deleteButton);
        ClickEffect.Opacity(addButton);
        ClickEffect.Opacity(deleteButton);
        this.addListener = listener;
        this.condition = condition;
        if (root) {
            if (condition instanceof LogicAll) {
                this.title.setText(this.context.getResources().getString(R.string.condition_format_all_root));
            } else {
                this.title.setText(condition.getFormattedName(this.context));
            }
            this.deleteButton.setEnabled(false);
            this.deleteButton.setVisibility(GONE);
        } else {
            this.addButton.setImageResource(R.drawable.ic_add_white);
            this.deleteButton.setImageResource(R.drawable.ic_delete_white);
            this.title.setText(condition.getFormattedName(this.context));
            this.title.setTextColor(getResources().getColor(R.color.white));
            if (condition instanceof LogicAll) {
                linLay.setBackgroundResource(R.drawable.bg_blue);
                //header.setBackgroundResource(R.drawable.condition_layout_back_blue);
                //setBackgroundResource(R.drawable.condition_border_back_blue);
            } else if (condition instanceof LogicAny) {
                linLay.setBackgroundResource(R.drawable.bg_pink);
                //header.setBackgroundResource(R.drawable.condition_layout_back_green);
                //setBackgroundResource(R.drawable.condition_border_back_green);
            } else if (condition instanceof LogicNone) {

                linLay.setBackgroundResource(R.drawable.bg_red);
                //header.setBackgroundResource(R.drawable.condition_layout_back_red);
                //setBackgroundResource(R.drawable.condition_border_back_red);
            }

            linLay.setPadding(8, 8, 8, 8);
        }
        if (getNestDepth() > MAX_NEST_DEPTH) {
            this.addButton.setEnabled(false);
            this.addButton.setVisibility(GONE);
        }
        this.addButton.setOnClickListener(new C01223(condition));
        this.deleteButton.setOnClickListener(new C01234(condition));

        addView(header, 0);
    }

    public static void bindLogicalCondition(Condition condition, LogicConditionLayout layout, OnConditionClickListener listener) {
        if (condition.getCategory() == 0) {
            Condition cur;
            layout.setOnConditionClickListener(listener);
            Iterator i$ = condition.getChildren().iterator();
            while (i$.hasNext()) {
                cur = (Condition) i$.next();
                if (cur.getCategory() == 1) {
                    layout.addCondition(cur, false);
                }
            }
            i$ = condition.getChildren().iterator();
            while (i$.hasNext()) {
                cur = (Condition) i$.next();
                if (cur.getCategory() == 0) {
                    bindLogicalCondition(cur, (LogicConditionLayout) layout.addCondition(cur, false), listener);
                }
            }
            return;
        }
        Log.w("SpeedUp_profiles", "Trying to bind non-logical base Condition!");
    }

    public void setOnConditionClickListener(OnConditionClickListener listener) {
        this.addListener = listener;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public LogicConditionLayout get() {
        return this;
    }

    public int getNestDepth() {
        int level = 0;
        Condition c = this.condition.getParent();
        while (c != null) {
            c = c.getParent();
            level++;
        }
        return level;
    }
}
