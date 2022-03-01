package com.example.counterbutton.customview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.counterbutton.R
import com.example.counterbutton.customview.attrs.Dimension
import com.example.counterbutton.customview.util.dp

class CounterButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val iconPlusChild = ImageView(context)
    private val iconMinusChild = ImageView(context)
    private val editTextChild = EditText(context)
    private val loadingChild = LottieAnimationView(context)
    private val containerChild = LinearLayout(context)
    private val containerEditTextChild = LinearLayout(context)
    private val containerLoadingChild = LinearLayout(context)

    var number: Int = minValue
        set(value) {
            if (value != number) {
                field = value
                editTextChild.setText(value.toString())
                editTextChild.requestLayout()
                editTextChild.setSelection(editTextChild.length())
            } else if (value != editTextChild.text.toString().toIntOrNull()) {
                field = value
                editTextChild.setText(value.toString())
                editTextChild.requestLayout()
                editTextChild.setSelection(editTextChild.length())
            }
        }
    var dimension = Dimension.M
        set(value) {
            field = value
            setupPadding()
            requestLayout()
        }
    var buttonType: ButtonType? = null
        set(value) {
            field = value
            setupViewByButtonType()
            setupBackground()
            setupBackgroundTint()
        }
    var isLoading = false
        set(value) {
            field = value
            handleLoading()
        }
    var isMinusEnabled = false
        set(value) {
            field = value
            iconMinusChild.isEnabled = value
        }
    var isPlusEnabled = false
        set(value) {
            field = value
            iconPlusChild.isEnabled = value
        }
    private var _maxValue = 999
    val maxValue: Int
        get() = _maxValue
    private var _minValue = 0
    val minValue: Int
        get() = _minValue
    private var hasCustomBackground = false
    private var hasCustomBackgroundTint = false

    init {
        receiveAttrs(attrs)
        setup()
    }

    private fun setup() {
        setupNumberField()
        setupIconPlus()
        setupIconMinus()
        setupContainer()
        setupLoading()

        setupPadding()
        setupMargin()
        handleCounterValue(number.toString(), Action.Check)
    }

    private fun setupNumberField() {
        editTextChild.filters += InputFilter.LengthFilter(maxValue.toString().length)
        editTextChild.setTextAppearance(TEXT_STYLE)
        editTextChild.inputType = InputType.TYPE_CLASS_NUMBER
        editTextChild.gravity = Gravity.CENTER
        editTextChild.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                number = handleCounterValue(s.toString(), Action.Check)
            }
        })
        containerEditTextChild.gravity = Gravity.CENTER
        containerEditTextChild.addView(editTextChild)
    }

    private fun setupLoading() {
        loadingChild.layoutParams = LayoutParams(LOADING_WIDTH, LOADING_HEIGHT)
        loadingChild.repeatCount = ValueAnimator.INFINITE
        loadingChild.setAnimation(R.raw.loading_black)
        containerLoadingChild.gravity = Gravity.CENTER
        containerLoadingChild.addView(loadingChild)
        addView(containerLoadingChild)
    }

    private fun setupIconPlus() {
        iconPlusChild.layoutParams = LinearLayout.LayoutParams(ICON_WIDTH, ICON_HEIGHT)
        iconPlusChild.setOnClickListener {
            number = handleCounterValue(valueStr = editTextChild.text.toString(), Action.Incrementing)
        }
    }

    private fun setupIconMinus() {
        iconMinusChild.layoutParams = LinearLayout.LayoutParams(ICON_WIDTH, ICON_HEIGHT)
        iconMinusChild.setOnClickListener {
            number = handleCounterValue(valueStr = editTextChild.text.toString(), Action.Decrementing)
        }
    }

    private fun setupContainer() {
        containerChild.gravity = Gravity.CENTER
        containerChild.addView(iconMinusChild)
        containerChild.addView(containerEditTextChild)
        containerChild.addView(iconPlusChild)
        addView(containerChild)
    }

    private fun setupMargin() {
        when (buttonType) {
            ButtonType.STANDARD -> (containerEditTextChild.layoutParams as? MarginLayoutParams)
                ?.setMargins(ET_MARGIN_START, 0, ET_MARGIN_END, 0)
            ButtonType.BORDERED -> (containerEditTextChild.layoutParams as? MarginLayoutParams)
                ?.setMargins(ET_MARGIN_START_BORDERED, 0, ET_MARGIN_END_BORDERED, 0)
            else -> {}
        }
    }

    private fun setupPadding() {
        if (buttonType == ButtonType.STANDARD) {
            when (dimension) {
                Dimension.L -> containerEditTextChild.setPadding(
                    ET_PADDING_START,
                    ET_PADDING_TOP_L,
                    ET_PADDING_END,
                    ET_PADDING_BOTTOM_L
                )
                Dimension.M -> containerEditTextChild.setPadding(
                    ET_PADDING_START,
                    ET_PADDING_TOP_M,
                    ET_PADDING_END,
                    ET_PADDING_BOTTOM_M
                )
                Dimension.S -> containerEditTextChild.setPadding(
                    ET_PADDING_START,
                    ET_PADDING_TOP_S,
                    ET_PADDING_END,
                    ET_PADDING_BOTTOM_S
                )
                Dimension.XS -> containerEditTextChild.setPadding(
                    ET_PADDING_START,
                    ET_PADDING_TOP_XS,
                    ET_PADDING_END,
                    ET_PADDING_BOTTOM_XS
                )
                else -> {}
            }
            editTextChild.setPadding(0, 0, 0, 0)
            setPadding(PADDING_START, PADDING_TOP, PADDING_END, PADDING_BOTTOM)
        } else {
            containerEditTextChild.setPadding(
                ET_PADDING_START_BORDERED,
                ET_PADDING_TOP_BORDERED,
                ET_PADDING_END_BORDERED,
                ET_PADDING_BOTTOM_BORDERED
            )
            editTextChild.setPadding(0, 0, 0, 0)
            setPadding(PADDING_START_BORDERED, PADDING_TOP_BORDERED, PADDING_END_BORDERED, PADDING_BOTTOM_BORDERED)
        }
    }

    private fun setupViewByButtonType() {
        when (buttonType) {
            ButtonType.STANDARD -> {
                editTextChild.layoutParams = LayoutParams(ET_WIDTH, LayoutParams.WRAP_CONTENT)
                iconPlusChild.setBackgroundResource(R.drawable.ic_plus_big_black)
                iconPlusChild.backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_counter_plus_minus_background_selector)
                iconMinusChild.setBackgroundResource(R.drawable.ic_minus_big_black)
                iconMinusChild.backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_counter_plus_minus_background_selector)
                containerEditTextChild.setBackgroundResource(R.drawable.button_counter_field_background)
                editTextChild.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
            ButtonType.BORDERED -> {
                editTextChild.layoutParams = LayoutParams(ET_WIDTH_BORDERED, LayoutParams.WRAP_CONTENT)
                iconPlusChild.setBackgroundResource(R.drawable.ic_plus_big_black)
                iconPlusChild.backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_counter_bordered_plus_minus_background_selector)
                iconMinusChild.setBackgroundResource(R.drawable.ic_minus_big_black)
                iconMinusChild.backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_counter_bordered_plus_minus_background_selector)
                editTextChild.setBackgroundColor(ContextCompat.getColor(context, R.color.white_0))
            }
            else -> {}
        }
    }

    private fun setupBackgroundTint() {
        if (!hasCustomBackgroundTint) {
            when (buttonType) {
                ButtonType.STANDARD -> backgroundTintList = ContextCompat.getColorStateList(
                    context, R.color.button_delivery_background_selector
                )
                ButtonType.BORDERED -> backgroundTintList = null
                else -> {}
            }
        }
    }

    private fun setupBackground() {
        if (!hasCustomBackground) {
            when (buttonType) {
                ButtonType.STANDARD -> setBackgroundResource(R.drawable.button_background)
                ButtonType.BORDERED -> setBackgroundResource(R.drawable.button_bordered_background)
                else -> {}
            }
        }
    }

    private fun receiveAttrs(attrs: AttributeSet? = null) {
        if (attrs != null) {
            try {
                val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CounterButton)
                if (ta.hasValue(R.styleable.CounterButton_android_backgroundTint)) {
                    hasCustomBackgroundTint = true
                }
                if (ta.hasValue(R.styleable.CounterButton_android_background)) {
                    hasCustomBackground = true
                }
                _minValue = ta.getInteger(R.styleable.CounterButton_cb_minValue, 0)
                _maxValue = ta.getInteger(R.styleable.CounterButton_cb_maxValue, 999)
                isMinusEnabled = ta.getBoolean(R.styleable.CounterButton_cb_minusEnabled, false)
                isPlusEnabled = ta.getBoolean(R.styleable.CounterButton_cb_plusEnabled, true)
                number = ta.getInteger(R.styleable.CounterButton_android_value, minValue)
                isLoading = ta.getBoolean(R.styleable.CounterButton_loading, false)
                dimension = Dimension.values()[ta.getInt(
                    R.styleable.CounterButton_cb_dimension,
                    Dimension.M.ordinal
                )]
                buttonType = ButtonType.values()[ta.getInt(
                    R.styleable.CounterButton_cb_buttonType,
                    ButtonType.STANDARD.ordinal
                )]
                ta.recycle()
            } catch (e: Exception) {
                number = number
                e.printStackTrace()
            }
        } else {
            _minValue = 0
            _maxValue = 999
            buttonType = ButtonType.STANDARD
            dimension = Dimension.M
            number = minValue
            isLoading = false
        }
    }

    private fun handleCounterValue(valueStr: String, action: Action): Int {
        return when (action) {
            Action.Incrementing -> {
                val state = validateValue((validateValue(valueStr).value + 1).toString())
                handleCounterButtonView(state)
                state.value
            }
            Action.Decrementing -> {
                val state = validateValue((validateValue(valueStr).value - 1).toString())
                handleCounterButtonView(state)
                state.value
            }
            Action.Check -> {
                val state = validateValue(valueStr)
                handleCounterButtonView(state)
                state.value
            }
        }
    }

    private fun handleCounterButtonView(state: State) {
        when (state) {
            is State.Success -> {
                isPlusEnabled = true
                isMinusEnabled = true
            }
            is State.FailureTooSmall -> {
                isPlusEnabled = true
                isMinusEnabled = false
            }
            is State.FailureTooBig -> {
                isPlusEnabled = false
                isMinusEnabled = true
            }
            else -> {}
        }
    }

    private fun handleLoading() {
        isEnabled = !isLoading
        if (isLoading) {
            loadingChild.playAnimation()
            loadingChild.visibility = View.VISIBLE
            editTextChild.visibility = View.INVISIBLE
            iconPlusChild.isClickable = false
            iconMinusChild.isClickable = false
        } else {
            loadingChild.cancelAnimation()
            loadingChild.visibility = View.INVISIBLE
            editTextChild.visibility = View.VISIBLE
            iconPlusChild.isClickable = true
            iconMinusChild.isClickable = true
        }
    }

    private fun validateValue(valueStr: String): State {
        val valueInt = (if (valueStr == "") minValue else valueStr.toIntOrNull())
            ?: return State.FailureIncapabilityToParse(number)
        return when {
            _maxValue <= valueInt -> State.FailureTooBig(maxValue)
            minValue >= valueInt -> State.FailureTooSmall(minValue)
            else -> State.Success(valueInt)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        if (isLoading && enabled) {
            isLoading = false
            return
        }
        super.setEnabled(enabled)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isLoading) {
            mergeDrawableStates(drawableState, STATE_LOADING)
        }
        return drawableState
    }

    private sealed class Action {
        object Incrementing : Action()
        object Decrementing : Action()
        object Check : Action()
    }

    private sealed class State(val value: Int) {
        class Success(value: Int) : State(value)
        class FailureTooBig(value: Int) : State(value)
        class FailureTooSmall(value: Int) : State(value)
        class FailureIncapabilityToParse(value: Int) : State(value)
    }

    enum class ButtonType {
        STANDARD, BORDERED
    }

    companion object {
        private val ET_PADDING_TOP_L = 13.dp
        private val ET_PADDING_BOTTOM_L = 14.dp
        private val ET_PADDING_TOP_M = 9.dp
        private val ET_PADDING_BOTTOM_M = 10.dp
        private val ET_PADDING_TOP_S = 5.dp
        private val ET_PADDING_BOTTOM_S = 6.dp
        private val ET_PADDING_TOP_XS = 1.dp
        private val ET_PADDING_BOTTOM_XS = 2.dp
        private val ET_PADDING_START = 8.dp
        private val ET_PADDING_END = 8.dp
        private val ET_PADDING_TOP_BORDERED = 5.dp
        private val ET_PADDING_BOTTOM_BORDERED = 5.dp
        private val ET_PADDING_START_BORDERED = 0.dp
        private val ET_PADDING_END_BORDERED = 0.dp
        private val PADDING_TOP = 4.dp
        private val PADDING_BOTTOM = 4.dp
        private val PADDING_START = 12.dp
        private val PADDING_END = 12.dp
        private val PADDING_TOP_BORDERED = 5.dp
        private val PADDING_BOTTOM_BORDERED = 5.dp
        private val PADDING_START_BORDERED = 6.dp
        private val PADDING_END_BORDERED = 6.dp
        private val ET_MARGIN_START = 12.dp
        private val ET_MARGIN_END = 12.dp
        private val ET_MARGIN_START_BORDERED = 2.dp
        private val ET_MARGIN_END_BORDERED = 2.dp
        private val ET_WIDTH = 120.dp
        private val ET_WIDTH_BORDERED = 62.dp
        private val LOADING_WIDTH = 16.dp
        private val LOADING_HEIGHT = 16.dp
        private val ICON_WIDTH = 24.dp
        private val ICON_HEIGHT = 24.dp
        private val STATE_LOADING = intArrayOf(R.attr.loading)
        private const val TEXT_STYLE = R.style.textBody1AC
    }
}
