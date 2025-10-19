package com.slack.exercise.ui.usersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.slack.exercise.R
import com.slack.exercise.model.UserSearchResult
import com.slack.exercise.ui.usersearch.model.UserSearchState
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Main fragment displaying and handling interactions with the view. We use the MVP pattern and
 * attach a Presenter that will be in charge of non view related operations.
 */
class UserSearchComposeFragment : DaggerFragment() {

  @Inject internal lateinit var presenter: UserSearchPresenter

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
    Column {
      state.userList.forEach { result ->
        Text(
            result.username,
            modifier = Modifier.padding(dimensionResource(R.dimen.item_margin)),
            style = MaterialTheme.typography.titleMedium,
        )
      }
    }
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
              setOf(UserSearchResult("James"), UserSearchResult("James123"))
          )
      )
    }
  }
}
