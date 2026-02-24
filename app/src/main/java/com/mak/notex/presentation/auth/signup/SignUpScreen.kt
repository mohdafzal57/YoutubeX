package com.mak.notex.presentation.auth.signup

import com.mak.notex.R
import androidx.compose.ui.res.stringResource
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mak.notex.presentation.auth.signup.SignUpContract.Effect
import com.mak.notex.presentation.auth.signup.SignUpContract.Event
import com.mak.notex.presentation.auth.signup.SignUpContract.State
import com.mak.notex.presentation.main.common.AppTextField
import com.mak.notex.presentation.main.common.PrimaryButton
import com.mak.notex.presentation.navigation.LocalSnackbarHostState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(Event.OnAvatarSelected(it)) }
    }

    val coverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onEvent(Event.OnCoverImageSelected(uri))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> onNavigateBack()
                is Effect.NavigateToSignIn -> onNavigateToSignIn()
                is Effect.OpenAvatarPicker -> avatarLauncher.launch("image/*")
                is Effect.OpenCoverImagePicker -> coverLauncher.launch("image/*")
                is Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }

                is Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    SignUpContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun SignUpContent(
    state: State,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            )
            .imePadding() // keyboard safe
            .verticalScroll(scrollState)
    ) {

        SignUpHeader(
            avatarUri = state.avatarUri,
            coverImageUri = state.coverImageUri,
            onAvatarClick = { onEvent(Event.OnAvatarPickerClicked) },
            onCoverClick = { onEvent(Event.OnCoverImagePickerClicked) }
        )

        Spacer(modifier = Modifier.height(64.dp))

        SignUpForm(
            state = state,
            onFullNameChanged = { onEvent(Event.OnFullNameChanged(it)) },
            onUsernameChanged = { onEvent(Event.OnUsernameChanged(it)) },
            onEmailChanged = { onEvent(Event.OnEmailChanged(it)) },
            onPasswordChanged = { onEvent(Event.OnPasswordChanged(it)) },
            onSignUpClicked = { onEvent(Event.OnSignUpClicked) },
            onSignInClicked = { onEvent(Event.OnNavigateToSignInClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(
            modifier = Modifier.height(
                WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                    .calculateBottomPadding() + 24.dp
            )
        )
    }
}

@Composable
private fun SignUpHeader(
    avatarUri: Uri?,
    coverImageUri: Uri?,
    onAvatarClick: () -> Unit,
    onCoverClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {

        CoverImageSection(
            coverImageUri = coverImageUri,
            onEditClick = onCoverClick
        )

        AvatarSection(
            avatarUri = avatarUri,
            onAvatarClick = onAvatarClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 40.dp)
        )
    }
}

@Composable
private fun CoverImageSection(
    coverImageUri: Uri?,
    onEditClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            if (coverImageUri != null) {
                AsyncImage(
                    model = coverImageUri,
                    contentDescription = stringResource(R.string.cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle dark overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            colors.scrim.copy(alpha = 0.08f)
                        )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(52.dp)
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .background(
                        colors.surface.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_cover),
                    tint = colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun AvatarSection(
    avatarUri: Uri?,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(modifier = modifier) {

        Card(
            modifier = Modifier
                .size(96.dp)
                .clickable { onAvatarClick() },
            shape = CircleShape,
            border = BorderStroke(
                3.dp,
                colors.background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {

                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = stringResource(R.string.avatar),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = colors.onSurfaceVariant
                    )
                }
            }
        }

        // Action Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .background(
                    color = colors.primary,
                    shape = CircleShape
                )
                .border(
                    2.dp,
                    colors.background,
                    CircleShape
                )
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (avatarUri == null)
                    Icons.Default.Add
                else
                    Icons.Default.Edit,
                contentDescription = stringResource(R.string.add_avatar),
                modifier = Modifier.size(16.dp),
                tint = colors.onPrimary
            )
        }
    }
}

@Composable
private fun SignUpForm(
    state: State,
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {

        // Title
        Text(
            text = stringResource(R.string.create_account),
            style = typography.headlineLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp
            ),
            color = colors.onSurface
        )

        // Subtitle
        Text(
            text = stringResource(R.string.enter_details_to_register),
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
        )

        // Fields
        AppTextField(
            value = state.fullName,
            onValueChange = onFullNameChanged,
            label = stringResource(R.string.full_name),
            error = state.validationErrors.fullName?.asString(),
            enabled = !state.isLoading,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        AppTextField(
            value = state.username,
            onValueChange = onUsernameChanged,
            label = stringResource(R.string.username),
            error = state.validationErrors.username?.asString(),
            enabled = !state.isLoading,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        AppTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = stringResource(R.string.email_address),
            error = state.validationErrors.email?.asString(),
            enabled = !state.isLoading,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        AppTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = stringResource(R.string.password),
            isPassword = true,
            error = state.validationErrors.password?.asString(),
            enabled = !state.isLoading,
            modifier = Modifier.padding(bottom = 36.dp)
        )

        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            UploadProgressCard(
                progress = state.uploadProgress / 100f,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        PrimaryButton(
            text = stringResource(R.string.sign_up),
            onClick = onSignUpClicked,
            loading = state.isLoading,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.already_have_account),
                style = typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            TextButton(onClick = onSignInClicked) {
                Text(
                    text = stringResource(R.string.sign_in),
                    style = typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun UploadProgressCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "uploadProgress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.uploading),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}