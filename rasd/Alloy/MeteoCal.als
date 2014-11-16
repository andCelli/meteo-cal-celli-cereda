//WE USE INT INSTEAD OF STRING BECAUSE ALLOY HAS A STRANGE BEHAVIOUR WITH STRINGS

//SIGNATURES

enum publicPrivate {pub, priv}
enum indoorOutdoor {indoor, outdoor}
enum notifyState {pending, archived}
enum proposalResponse {moved, ignored}
enum inviteResponse{accepted, declined}

sig User{
	id: one Int,
	Name: one Int,
	Surname: one Int,
	username: one Int,
	password: one Int,
	email: one Int,
	calendar: one Calendar,
	eventsCreated: set Event,
	notifications: set Notify
}

sig Calendar{
	type: one publicPrivate,
	events: set Event
}

sig Event{
	id: one Int,
	//for our purposes dates and hours can be simply modeled
	date: one Int,
	startHour: one Int,
	endHour: one Int,
	type: one indoorOutdoor,
	location: lone Place,
	forecast: lone Forecast
}

sig Place{
	id: one Int,
	name: one Int
}

sig Forecast{
	day: one Int,
	hour: one Int,
	forecastDay: one Int,
	forecastTime: one Int,
	place: one Place
}

sig Notify{
	id: one Int,
	state: one notifyState,
	referredEvent: one Event
}

sig EventChanged extends Notify{}

sig BadWeatherAlert extends Notify{}

sig SunnyDayProposal extends Notify{
	response: lone proposalResponse
}

sig Invite extends Notify{
	response: lone inviteResponse
}


//FACTS

//the ids, username and emails are unique
fact uniqueId{
	all u,u':User | u != u' implies (u.id != u'.id) and (u.email != u'.email) and (u.username != u'.username)
	all e,e':Event | e != e' implies e.id != e'.id
	all p,p':Place | p != p' implies p.id != p'.id
	all n,n':Notify | n != n' implies n.id != n'.id
}

//a calendar is owned by exactly one user
fact calendarNotShared{
	all c:Calendar | one u:User | u.calendar = c
}

//the events are allways in the calendar of the creator
fact creatorTakePartInEvents{
	all u:User, e:Event | e in u.eventsCreated implies e in u.calendar.events
}

//all the events have exactly one creator
fact allEventsHaveCreator{
	all e:Event | one u:User | e in u.eventsCreated
}

//all the notifications has at least one receiver
fact EveryNotificationHasAReceiver{
    all n:Notify | some u:User | n in u.notifications
}

//if an event has a notification it must refer to the place where the event takes place
fact CoherentForecast{
	all e:Event, f:Forecast | f = e.forecast implies f.place = e.location
}

//we only have forecasts related to some event
fact UsefulForecast{
	all f:Forecast | some e:Event | e.forecast = f
}

//we only keep track of updated forecasts, so we have only one forecast per place
fact UpdatedForecasts{
	all p:Place | lone f:Forecast | f.place = p
}

//if an event is outdoor it must have a place
fact OutdoorEventsHaveAPlace{
	all e:Event | e.type = outdoor implies one p:Place | e.location = p
}

//only outdoor events receive forecasts
fact OnlyOutdoorEventsHaveForecasts{
	all e:Event | e.type = outdoor implies one f:Forecast | e.forecast = f
	all f:Forecast, e:Event | e.forecast = f implies e.type = outdoor
}

//we keep track only of place where there is at least one event
fact EveryPlaceReferToAnEvent{
    all p:Place | some e:Event | e.location=p
}

//the starting hour of an event is before the ending hour
fact TimeConsistentEvents{
	all e:Event | e.startHour < e.endHour
}

//forecasts of an event refers to the the same day of the event
fact ForecastSameDayEvent{
	all e:Event, f:Forecast | e.forecast = f implies e.date = f.forecastDay
}

//forecasts of an event refers to a time within the duration of the event
fact CorrectForecastTiming{
	all e: Event, f:Forecast | e.forecast=f  implies (e.startHour =< f.forecastTime) and (f.forecastTime =< e.endHour)
}

//the date in which forecasts are made is before or equal to the date of the prediction
fact ForecastBeforeForecastedDay{
	all f:Forecast | (f.day < f.forecastDay) or (f.day = f.forecastDay and f.hour < f.forecastTime)
}

//only user that do NOT partecipate yet to an event can be invited to it
fact InviteOnlyNonPartecipants{
	all i:Invite, u:User | i in u.notifications implies not i.referredEvent in u.calendar.events
}

//users that don't partecipate in an event can't receive any notification but invites
fact NotifyOnlyPartecipants{
	all e:Event, u:User | e in u.calendar.events implies
		no n:(Notify-Invite) | n in u.notifications
}

//when the creator of an event modifies its details he doesn't receive relative EventChangedNotification 
fact EventChangedNotSentToCreator{
	all e:Event, u:User | e in u.eventsCreated implies
		no e:EventChanged | e in u.notifications
}

//SunnyDayNotification and BadWeatherAlerts are related only to outdoor events
fact NotificationsOnlyForOutdoor{
	all n:(BadWeatherAlert+SunnyDayProposal), e:Event | e.type = outdoor implies not e=n.referredEvent
}

//if an invite or a sunny day proposal is archived it has to have a response.
fact ResposeToNotifications{
	all n:Invite | (n.state = pending implies no r:inviteResponse | r=n.response) and
						(n.state = archived implies one r:inviteResponse | r=n.response)

	all n:SunnyDayProposal | (n.state = pending implies no r:proposalResponse | r=n.response) and
						(n.state = archived implies one r:proposalResponse | r=n.response)
} 

//an event can have a related SunnyDayProposal/BadWeatherAlert only if the event has a forecast related
fact WeatherNotificationOnlyToForecastedEvents{
	all e:Event, n:(BadWeatherAlert+SunnyDayProposal) | n.referredEvent = e implies some f:Forecast | e.forecast = f
}

//EventChanged, SunnyDayProposal, BadWeatherAlert can be received only by users that have the related event in their calendar
fact ConsistentNotifications{
	all n:(EventChanged+SunnyDayProposal+BadWeatherAlert), u:User | n in u.notifications implies n.referredEvent in u.calendar.events 
}

//PREDICATES

//add an event to a calendar
pred addEvent[c, c':Calendar, e:Event]{
	c'.events=c.events + e
}

//Remove an event from a calendar. Also remove it from the calendar of all event's participants. (The event creator is removing it)
pred removeEvent[c, c':Calendar, e:Event]{
	c'.events=c.events - e

	all u:User |  e in u.calendar.events
	implies u.calendar.events= u.calendar.events - e
}

//change the location of an event
pred changeEventLocation[e:Event, p:Place]{
	e.location = p
}

//send a notify to an user
pred sendNotify[u,u':User, n:Notify]{
	u'.notifications = u.notifications+n
}

//read a notification
pred readNotify[u,u':User, n:Notify]{
	u'.notifications = u.notifications - n
}

//accept an invite
pred acceptInvite[u,u':User, n:Invite]{
	(u'.notifications = u.notifications - n) and (u'.calendar.events = u.calendar.events + n.referredEvent)
}

//decline an invite
pred declineInvite[u,u':User, n:Invite]{
	u'.notifications = u.notifications - n
}

//ASSERTIONS

//adding and removing an event doesn't change the calendar
assert addRemoveEvent{
	all c, c', c'': Calendar, e: Event |
		not (e in c.events) and addEvent[c, c', e] and removeEvent[c', c'', e]
	implies
		c.events = c''.events
}check addRemoveEvent

//deleting an event implies that no user still have the event in the calendar
assert deleteDestroyEvent{
	all c,c':Calendar, e:Event |
		removeEvent[c,c',e]
	implies
		no u: User | e in u.calendar.events
}check deleteDestroyEvent

//changing the place of an event will force the change of the weather forecast
assert changeLocation{
	all e:Event, p:Place |
		(changeEventLocation[e, p]) and (some f:Forecast | e.forecast = f)
	implies
		e.forecast.place = p
}check changeLocation


//decline an invite doesn't change the calendar
assert declineDoesNotChangeCalendar{
	all u,u':User, n:Invite |
		 (u = u') and (n in u.notifications) and (declineInvite[u,u',n])
	implies
		u'.calendar = u.calendar
}check declineDoesNotChangeCalendar

//adding and removing a notification doesn't change the notification list
assert addRemoveNotification{
	all u,u': User, n:Notify |
		not (n in u.notifications) and sendNotify[u,u',n] and readNotify[u,u',n]
	implies
		u.notifications = u'.notifications
}check addRemoveNotification

//an event can have a related SunnyDayProposal/BadWeatherAlert only if the event is outdoor
assert WeatherNotificationOnlyToOutdoorEvents{
	all e:Event | e.type = indoor implies
		no n:(SunnyDayProposal + BadWeatherAlert) | n.referredEvent = e
}check WeatherNotificationOnlyToOutdoorEvents

//PREDICATES

//there can be events without a forecast (i.e. indoor events)
pred showEventsWithoutForecasts(e:Event){
	no f:Forecast | f.place = e.location
} run showEventsWithoutForecasts

//there can be events without a place (i.e. indoor events)
pred showEventsWithoutPlace(e:Event){
	no p:Place | p = e.location
} run showEventsWithoutPlace

//there can be indoor and outdoor events
pred showBothIndoorOutdoor(e,e':Event){
	e.type = indoor and e'.type = outdoor
} run showBothIndoorOutdoor

//simple show
pred showSimple{
	#User = 1
	#Event = 1
}run showSimple

//show a simple world with an Invite notification
pred showInvite{
	#User = 2
	#Event = 1
	#Invite = 1
	#Notify =1
}run showInvite

//show a simple world with a weather forecast
pred showForecast{
	#User = 2
	#Event = 1
	#Forecast = 1
	#Notify = 0
}run showForecast

//show a complex world
pred show{}
run show
