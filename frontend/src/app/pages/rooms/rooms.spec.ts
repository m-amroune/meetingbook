
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';

import {
  MeetingRoomService,
  type MeetingRoom,
} from '../../core/services/meeting-room';
import { Rooms } from './rooms';

describe('Rooms', () => {
  let fixture: ComponentFixture<Rooms>;
  let apiResponse: Observable<MeetingRoom[]>;

  beforeEach(async () => {
    apiResponse = of([
      {
        id: 2,
        name: 'Simone Veil',
        location: 'Étage 1',
        capacity: 6,
        equipment: ['Écran'],
        status: 'ACTIVE',
      },
    ]);

    const meetingRoomServiceMock = {
      getAll: () => apiResponse,
    };

    await TestBed.configureTestingModule({
      imports: [Rooms],
      providers: [
        provideRouter([]),
        {
          provide: MeetingRoomService,
          useValue: meetingRoomServiceMock,
        },
      ],
    }).compileComponents();
  });

  async function renderComponent(): Promise<HTMLElement> {
    fixture = TestBed.createComponent(Rooms);
    fixture.detectChanges();
    await fixture.whenStable();

    return fixture.nativeElement;
  }

  it('should display rooms returned by the service', async () => {
    const element = await renderComponent();

    expect(element.textContent).toContain('Simone Veil');
    expect(element.textContent).toContain('6 personnes');
    expect(element.textContent).toContain('Étage 1');
  });

  it('should display an error when loading rooms fails', async () => {
    apiResponse = throwError(() => new Error('API unavailable'));

    const element = await renderComponent();
    const alert = element.querySelector('[role="alert"]');

    expect(alert).not.toBeNull();
    expect(alert?.textContent).toContain(
      'Impossible de charger les salles.'
    );
    expect(element.textContent).not.toContain('Simone Veil');
  });
});


