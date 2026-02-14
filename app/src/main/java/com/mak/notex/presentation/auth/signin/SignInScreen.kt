package com.mak.notex.presentation.auth.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.notex.R
import com.mak.notex.data.utils.ObserveAsEvents
import com.mak.notex.presentation.common.AppTextField
import com.mak.notex.presentation.common.PrimaryButton
import com.mak.notex.presentation.navigation.LocalSnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun SignInRoute(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is SignInEffect.NavigateToHome -> onNavigateToHome()
            is SignInEffect.ShowSnackbarError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        effect.message.asString(context)
                    )
                }
            }
        }
    }

    SignInScreen(
        state = state,
        onIdentifierChanged = {
            viewModel.handleIntent(SignInIntent.IdentifierChanged(it))
        },
        onPasswordChanged = {
            viewModel.handleIntent(SignInIntent.PasswordChanged(it))
        },
        onSignInClick = {
            viewModel.handleIntent(SignInIntent.SignInClicked)
        },
        onNavigateToSignUp = onNavigateToSignUp,
        onForgotPasswordClick = {},
        onGoogleSignInClick = {}
    )
}

@Composable
fun SignInScreen(
    state: SignInState,
    onIdentifierChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignInClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {

    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(120.dp))

            Surface(
                modifier = Modifier.weight(20f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(R.drawable.youtube_splash2_ic),
                        modifier = Modifier.size(100.dp),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AppTextField(
                value = state.identifier,
                onValueChange = onIdentifierChanged,
                label = stringResource(id = R.string.email_or_username),
//                leadingIcon = Icons.Default.Email,
                enabled = !state.isLoading,
                error = state.identifierError?.asString()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                label = stringResource(id = R.string.password),
//                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                enabled = !state.isLoading,
                error = state.passwordError?.asString()
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = stringResource(id = R.string.log_in),
                onClick = onSignInClick,
                loading = state.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = stringResource(id = R.string.forgot_password),
                        // Using onSurfaceVariant for a subtle "forgot password" link
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(22f))

            // Modern Google Sign-In Button style
            OutlinedButton(
                onClick = onGoogleSignInClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(100),
                // Use outline color for the border to match industry standard input fields
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.continue_with_google),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        // Primary color draws the user to the secondary CTA
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SignInScreenLoadingPreview() {
//    NoteXTheme() {
//        SignInScreen(
//            state = SignInState(
//                identifier = "",
//                password = "",
//                isLoading = false,
//                identifierError = null,
//                passwordError = null
//            ),
//            onIdentifierChanged = {},
//            onPasswordChanged = {},
//            onSignInClick = {},
//            onNavigateToSignUp = {},
//            onForgotPasswordClick = {},
//            onGoogleSignInClick = {}
//        )
//    }
//}
