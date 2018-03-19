package no.fint.provider.tilganger.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.administrasjon.personal.Personalressurs;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import no.fint.model.ressurser.tilganger.Identitet;
import no.fint.model.ressurser.tilganger.Rettighet;
import no.fint.model.ressurser.tilganger.TilgangerActions;
import no.fint.provider.adapter.event.EventResponseService;
import no.fint.provider.adapter.event.EventStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventHandlerService {

    @Autowired
    private EventResponseService eventResponseService;

    @Autowired
    private EventStatusService eventStatusService;


    public void handleEvent(Event event) {
        if (event.isHealthCheck()) {
            postHealthCheckResponse(event);
        } else {
            if (event != null && eventStatusService.verifyEvent(event).getStatus() == Status.ADAPTER_ACCEPTED) {
                TilgangerActions action = TilgangerActions.valueOf(event.getAction());
                Event<FintResource> responseEvent = new Event<>(event);

                responseEvent.setStatus(Status.ADAPTER_RESPONSE);
                switch (action) {
                    case GET_ALL_RETTIGHET:
                        onGetAllRettighet(responseEvent);
                        break;
                    default:
                        responseEvent.setStatus(Status.ADAPTER_REJECTED);
                }

                eventResponseService.postResponse(responseEvent);
            }
        }
    }


    private void onGetAllRettighet(Event<FintResource> responseEvent) {

        responseEvent.addData(FintResource.with(RettighetsFactory.createRettighet("no.fint.ist.vigovoksen", "Vigo Voksen", "Tilgang til Vigo Voksen")));
        responseEvent.addData(FintResource.with(RettighetsFactory.createRettighet("no.fint.ist.vigoot", "Vigo OT", "Tilgang til Vigo OT")));
        responseEvent.addData(FintResource.with(RettighetsFactory.createRettighet("no.fint.ist.vigoinntak", "Vigo inntak", "Tilgang til Vigo inntak")));

    }

    public void postHealthCheckResponse(Event event) {
        Event<Health> healthCheckEvent = new Event<>(event);
        healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);

        if (healthCheck()) {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_HEALTHY.name()));
        } else {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_UNHEALTHY));
            healthCheckEvent.setMessage("The adapter is unable to communicate with the application.");
        }

        eventResponseService.postResponse(healthCheckEvent);
    }

    private boolean healthCheck() {
        return true;
    }

}
