Здесь пример самого просто окна в котором при нажатии на кнопку создается и отображается новый инвайт в указанный 
ивент, а над кнопокой расположены ивенты пользователя(в данном коде userId = "3")

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val userId: String = "3"

    val eventFlow: StateFlow<List<Event>> = eventRepository
        .observeEventsByUserId(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invitesFlow: StateFlow<List<Event>> = userRepository
        .observeInvites(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun acceptInvite(eventId: String) {
        viewModelScope.launch {
            userRepository.acceptInvite(userId, eventId)
        }
    }

    fun declineInvite(eventId: String) {
        viewModelScope.launch {
            userRepository.declineInvite(userId, eventId)
        }
    }
    fun sendInvite() {
        viewModelScope.launch {
            eventRepository.sendInvite("30a2944d-bd98-4ff5-96af-85c28da19fc2", userId)
        }
    }
}

@Composable
fun EventsScreen(
    viewModel: EventsViewModel = hiltViewModel()
) {
    val events by viewModel.eventFlow.collectAsState()
    val invites by viewModel.invitesFlow.collectAsState()
    val selectedEvent by viewModel.selectedEvent

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Мои события", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(events) { event ->
                EventCard(
                    event = event
                )
            }
        }

        Button(
            onClick = { viewModel.sendInvite() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить приглашение")
        }

        if (invites.isNotEmpty()) {
            Spacer(Modifier.fillMaxWidth().height(40.dp))
            LazyColumn {
                items(invites) { invite ->
                    InviteCard(
                        event = invite,
                        onAccept = { viewModel.acceptInvite(invite.eventId) },
                        onDecline = { viewModel.declineInvite(invite.eventId) }
                    )
                }
            }
            Spacer(Modifier.fillMaxWidth().height(40.dp))
        } else {
            Spacer(Modifier.fillMaxWidth().height(50.dp))
            Text("Нет приглашений")
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, fontWeight = FontWeight.Bold)
            Text(event.description)
            Text("Время: ${event.time}")
        }
    }
}

@Composable
fun InviteCard(event: Event, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Приглашение: ${event.title}", fontWeight = FontWeight.Bold)
            Text(event.description)
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onDecline) {
                    Text("Отклонить")
                }
                TextButton(onClick = onAccept) {
                    Text("Принять")
                }
            }
        }
    }
}

И еще примеры находятся в MapViewModel, но там надо разобраться
    