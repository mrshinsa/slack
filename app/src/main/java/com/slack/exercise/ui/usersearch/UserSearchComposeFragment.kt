package com.slack.exercise.ui.usersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.slack.exercise.R
import com.slack.exercise.model.UserSearchResult
import com.slack.exercise.ui.usersearch.model.UserSearchState
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.String

/**
 * Main fragment displaying and handling interactions with the view. We use the MVP pattern and
 * attach a Presenter that will be in charge of non view related operations.
 */
class UserSearchComposeFragment : DaggerFragment() {

    @Inject
    internal lateinit var presenter: UserSearchPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val state by presenter.getUserSearchState().collectAsState(UserSearchState(emptySet()))

                    UserSearchScreen(state) { presenter.onQueryTextChange(it) }
                }
            }
        }
    }
}

/**
 * The User Search Screen layout.
 *
 * @param state Ui state for the User Search Screen.
 * @param modifier the modifier to apply to this layout.
 * @param onQueryTextChange the callback to be invoked when the search query changes.
 */
@Composable
private fun UserSearchScreen(
    state: UserSearchState,
    modifier: Modifier = Modifier,
    onQueryTextChange: (String) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        UserSearchBar(
            onQueryChange = onQueryTextChange,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.userList.toList()) { result ->
                UserSearchItem(user = result)
            }
        }
    }
}

@Composable
private fun UserSearchItem(user: UserSearchResult) {
    val context = LocalContext.current  // Capture context here
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.search_background))
            .clickable {
                // Show toast with user display name
                Toast.makeText(
                    context,
                    "Selected: ${user.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.search_row_height))
                .padding(start = dimensionResource(R.dimen.search_avatar_left_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            // Load the avatar images asynchronously so that we don't block the UI thread
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(dimensionResource(R.dimen.search_avatar_size))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.search_avatar_corner_radius))),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.search_avatar_name_spacing)))

            // Name and Username
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.displayName,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Default, // Will use Lato Bold from font resources
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.search_name_color),
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.search_name_username_spacing)))

                Text(
                    text = user.username,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Default, // Will use Lato Regular from font resources
                    fontWeight = FontWeight.Normal,
                    color = colorResource(R.color.search_username_color),
                    lineHeight = 28.sp
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.search_divider_height))
                .background(colorResource(R.color.search_divider_color))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSearchBar(onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var queryText by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        query = queryText,
        onQueryChange = {
            queryText = it
            onQueryChange(it)
        },
        onSearch = { isActive = false },
        active = isActive,
        onActiveChange = {},
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_users_hint)) },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = stringResource(R.string.search_users_hint),
            )
        },
        trailingIcon = {
            if (queryText.isNotEmpty()) {
                IconButton(
                    onClick = {
                        queryText = ""
                        onQueryChange("")
                    }) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.clear_search),
                    )
                }
            }
        }) {
        //
    }
}

@Composable
@Preview
private fun Preview() {
    MaterialTheme {
        Surface {
            UserSearchScreen(
                state = UserSearchState(
                    //   UserSearchResult(val avatarUrl: String, val displayName: String, val username: String)
                    setOf(
                        UserSearchResult(
                            "https://pngtree.com/freepng/user-vector-avatar_4830521.html",
                            "James Lee",
                            "James"
                        ),
                        UserSearchResult(
                            "https://pngtree.com/freepng/boy-user-avatar-vector-icon-free_4827808.html",
                            "James Lee the Third",
                            "James123"
                        )
                    )
                )
            )
        }
    }
}
