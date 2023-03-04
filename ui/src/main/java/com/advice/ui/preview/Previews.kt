package com.advice.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview(uiMode = UI_MODE_NIGHT_YES, group = "dark", showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_NO, group = "light", showBackground = true)
annotation class LightDarkPreview