package core

import io.grpc.ManagedChannelBuilder
import event._

object Api {

	val channel = ManagedChannelBuilder.forAddress(sys.env("GRPC_URL"),sys.env("GRPC_PORT").toInt).usePlaintext().build

	def generateEvents(amount: Int): Option[List[models.Event]] = {
		
		val request = EventRequest(amount)

		val blockingStub = EventServiceGrpc.blockingStub(channel)
		val reply: EventResponse = blockingStub.eventService(request)

		Some(reply.events.toList.map(e => models.Event(
			e.citizen, 
			e.message, 
			e.latitude, 
			e.longitude,
			e.date.get.seconds,
			e.battery,
			e.temperature,
			e.country
		)))
	}
}
