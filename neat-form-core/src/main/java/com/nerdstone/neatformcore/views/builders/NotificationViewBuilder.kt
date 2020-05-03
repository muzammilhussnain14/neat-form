package com.nerdstone.neatformcore.views.builders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nerdstone.neatformcore.R
import com.nerdstone.neatformcore.domain.builders.ViewBuilder
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.Constants.NotificationTypes
import com.nerdstone.neatformcore.utils.DialogUtil
import com.nerdstone.neatformcore.utils.Utils
import com.nerdstone.neatformcore.utils.ViewUtils
import com.nerdstone.neatformcore.views.widgets.NotificationNFormView
import java.util.*

open class NotificationViewBuilder(final override val nFormView: NFormView) : ViewBuilder {

    private lateinit var notificationTitle: TextView
    private lateinit var notificationText: TextView
    private lateinit var notificationCancelIcon: ImageView
    private lateinit var notificationTypeIcon: ImageView
    private lateinit var notificationInfoIcon: ImageView
    private val notificationView = nFormView as NotificationNFormView
    private var currentViewProps: MutableMap<String, Any>? = null


    private val notificationResourceMap = mutableMapOf(
        NotificationTypes.INFO to Pair(R.color.toast_notification_info, R.drawable.ic_check),
        NotificationTypes.ERROR to Pair(R.color.toast_notification_error, R.drawable.ic_error),
        NotificationTypes.SUCCESS to Pair(R.color.toast_notification_success, R.drawable.ic_success),
        NotificationTypes.WARNING to Pair(R.color.toast_notification_warning, R.drawable.ic_warning)
    )

    enum class NotificationProperties {
        DISMISSIBLE, NOTIFICATION_TYPE, NOTIFICATION_DIALOG_TITLE, NOTIFICATION_DIALOG_TEXT,
        TEXT, TITLE, TEXT_COLOR, BACKGROUND_COLOR,
    }

    override val acceptedAttributes = Utils.convertEnumToSet(NotificationProperties::class.java)

    override fun buildView() {
        currentViewProps = notificationView.viewProperties.viewAttributes
        val layout = View.inflate(
            notificationView.context, R.layout.toast_notification_layout, notificationView
        )
        with(layout) {
            notificationTitle = findViewById(R.id.notificationTitleTextView)
            notificationCancelIcon = findViewById(R.id.notificationCancelIcon)
            notificationInfoIcon = findViewById(R.id.notificationInfoIcon)
            notificationTypeIcon = findViewById(R.id.notificationTypeIcon)
            notificationText = findViewById(R.id.notificationTextTextView)
        }
        val notificationType =
            currentViewProps?.get(
                NotificationProperties.NOTIFICATION_TYPE.name
                    .toLowerCase(Locale.getDefault())
            )
                .toString()
        setupNotificationType(notificationType)
        ViewUtils.applyViewAttributes(nFormView, acceptedAttributes, this::setViewProperties)
    }

    override fun setViewProperties(attribute: Map.Entry<String, Any>) {
        when (attribute.key.toUpperCase(Locale.getDefault())) {
            NotificationProperties.DISMISSIBLE.name -> {
                notificationCancelIcon.apply {
                    if (attribute.value.toString() == "true" || attribute.value.toString() == "yes")
                        notificationCancelIcon.apply {
                            visibility = View.VISIBLE
                            setOnClickListener { notificationView.visibility = View.GONE }
                        }
                }
            }
            NotificationProperties.TITLE.name -> {
                notificationTitle.apply {
                    visibility = View.VISIBLE
                    text = attribute.value.toString()
                }
            }
            NotificationProperties.TEXT.name -> {
                notificationText.apply {
                    visibility = View.VISIBLE
                    text = attribute.value.toString()
                }
            }
            NotificationProperties.TEXT_COLOR.name -> {
                with(Color.parseColor(attribute.value.toString())) {
                    notificationText.setTextColor(this)
                    notificationTitle.setTextColor(this)
                }
            }
            NotificationProperties.BACKGROUND_COLOR.name -> {
                notificationView.setBackgroundColor(Color.parseColor(attribute.value.toString()))
            }
            NotificationProperties.NOTIFICATION_DIALOG_TEXT.name, NotificationProperties.NOTIFICATION_DIALOG_TITLE.name -> {
                notificationInfoIcon.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val dialogTitleAndText = getDialogTitleAndText()
                        DialogUtil.createAlertDialog(
                            notificationView.context,
                            dialogTitleAndText.first,
                            dialogTitleAndText.second
                        ).apply {
                            setCancelable(true)
                            setNegativeButton(R.string.ok) { _, _ -> return@setNegativeButton }
                        }.show()
                    }
                }
            }
        }
    }

    private fun setupNotificationType(type: String) {
        notificationResourceMap[type]?.let {
            ContextCompat.getColor(notificationView.context, it.first)
        }?.let { notificationView.setBackgroundColor(it) }
        notificationResourceMap[type]?.second?.let { notificationTypeIcon.setImageResource(it) }
    }

    private fun getDialogTitleAndText(): Pair<String, String> {
        val title =
            currentViewProps?.get(
                NotificationProperties.NOTIFICATION_DIALOG_TITLE.name.toLowerCase(Locale.getDefault())
            ).toString()
        val text =
            currentViewProps?.get(
                NotificationProperties.NOTIFICATION_DIALOG_TEXT.name.toLowerCase(Locale.getDefault())
            ).toString()
        return Pair(title, text)
    }
}