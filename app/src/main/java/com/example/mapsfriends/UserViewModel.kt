package com.example.mapsfriends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository:UserRepository
) : ViewModel(){
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser : StateFlow<User?> = _currentUser

    private val _userFriends = MutableStateFlow<List<User>?>(emptyList())
    val userFriends: StateFlow<List<User>?> = _userFriends


    val list = listOf<String>("3","4")
    fun getUser(userId: String){
        viewModelScope.launch {

            _currentUser.value = userRepository.getUserById(userId)
            _userFriends.value = userRepository.getFriendsList(userId)
            userRepository.updateUserLocation(_currentUser.value!!.user_id, GeoPoint(54.2331,55.5444))
            userRepository.setUser("1","Joske","",list, GeoPoint(55.31231,54.41512))
        }
    }
}

@Composable
fun Screen (viewModel: UserViewModel = (hiltViewModel()),
            eventViewModel: EventViewModel = hiltViewModel()){
    val user = viewModel.currentUser.collectAsState()
    val userFriends = viewModel.userFriends.collectAsState()

    Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ){
        Text("${userFriends}}")

        Button(onClick = { viewModel.getUser("3") }) { Text("Click") }
        Button(onClick = { eventViewModel.setEvent()}) { Text("SetEvent") }
    }
}

@Preview
@Composable
fun Preview(){
    Screen()
}
