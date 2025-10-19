This exercise focused on two main topics.
    #1 Handle the denylist and update it accordingly
    #2 Format the UI according to the spec from the design team

For #1, I've modified the UserSearchResultDataProviderImpl class to do the following:
    1. Load the denylist at startup and store it in memory
    2. Check the search term against the in-memory denylist to determine if the Slack API should be called
    3. Maintain a separate dynamic runtime denylist for search terms that return no results from the API. This runtime denylist is kept separate to prevent corruption of the original denylist
    4. For subsequent searches, check both the original in-memory denylist and the dynamic runtime denylist

For #2, I've modified the UserSearchComposeFragment class to do the following:
    1. Add the UserSearchItem composable to handle the UI presentation of each element in the list, including the avatar, display name, and username
    2. Use AsyncImage to load images asynchronously
    3. Make the list scrollable
    4. Handle item clicks for future expandability

As an additional tool, I've added UserSearchResultDataProviderImplTest to test the search results from the Slack API