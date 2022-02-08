//TODO rename the packege
package com.openclassrooms.go4lunch.viewmodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.ViewSnapshot;
import com.google.firebase.firestore.model.DocumentKey;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class WorkmateRepositoryTest {
    Application myApplication;
    WorkmateRepository t;

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firestore;
    @Mock
    CollectionReference workmateRef;

    @Captor
    ArgumentCaptor<EventListener<QuerySnapshot>> eventSnapshotListenerCaptor;

    ListenerRegistration listenerRegistration;

    @Mock
    QuerySnapshot querySnapshot;

    List<DocumentSnapshot> listDocumentSnapshot = new ArrayList<>();

    @Test
    public void Initialization() {
        myApplication = new Application();
        if (t!=null) {
            t = WorkmateRepository.getWorkmateRepository(firestore);
        } else {
            when(firestore.collection("workmates")).thenReturn(workmateRef);
            when(workmateRef.addSnapshotListener(any())).thenReturn(listenerRegistration);
            t = new WorkmateRepository(firestore);
            verify(workmateRef).addSnapshotListener(eventSnapshotListenerCaptor.capture());
            assertNotNull(eventSnapshotListenerCaptor.getValue());
            listDocumentSnapshot = new ArrayList<>();
            when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
            eventSnapshotListenerCaptor.getValue().onEvent(querySnapshot, null);
        }
    }

    @Test
    public void DbModification() {
        GetWorkmatesEmpty();
        myApplication = new Application();
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document.toObject(Workmate.class)).thenReturn(workmate1);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        eventSnapshotListenerCaptor.getValue().onEvent(querySnapshot, null);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(1, workmates.getValue().size());
        assertNotNull(workmates.getValue().get(0));
        assertEquals("vjraymon@gmail.com",workmates.getValue().get(0).getEmail() );
        assertEquals("Jean-Raymond Vieux",workmates.getValue().get(0).getName() );

    }

    @Mock
    Task<QuerySnapshot> task;
    @Captor
    ArgumentCaptor<OnCompleteListener<QuerySnapshot>> eventGetListenerCaptor;
    @Captor
    ArgumentCaptor<OnFailureListener> eventGetFailureListenerCaptor;

    LiveData<List<Workmate>> workmates;

    @Test
    public void GetWorkmatesError() {
        Initialization();
        when(workmateRef.get()).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        workmates = t.getWorkmates();
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(task.isSuccessful()).thenReturn(false);
        eventGetListenerCaptor.getValue().onComplete(task);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertTrue(workmates.getValue().isEmpty());
    }

    @Test
    public void GetWorkmatesException() {
        Initialization();
        when(workmateRef.get()).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        workmates = t.getWorkmates();
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));
        assertNotNull(workmates);
        assertNull(workmates.getValue());
    }

    @Mock
    Task<QuerySnapshot> task1, task2, task3, task4;

    @Mock
    DocumentSnapshot document, document2;

    @Test
    public void GetWorkmatesEmpty() {
        Initialization();
        when(workmateRef.get()).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        workmates = t.getWorkmates();
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(task.getResult()).thenReturn(querySnapshot);
        when(task.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        eventGetListenerCaptor.getValue().onComplete(task);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertTrue(workmates.getValue().isEmpty());
    }

    @Test
    public void GetWorkmates1Record() {
        Initialization();
        when(workmateRef.get()).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        workmates = t.getWorkmates();
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(task.getResult()).thenReturn(querySnapshot);
        when(task.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        when(document.toObject(Workmate.class)).thenReturn(myself);
        eventGetListenerCaptor.getValue().onComplete(task);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(1, workmates.getValue().size());
        assertNotNull(workmates.getValue().get(0));
        assertEquals("vjraymon@gmail.com",workmates.getValue().get(0).getEmail() );
        assertEquals("Jean-Raymond Vieux",workmates.getValue().get(0).getName() );
    }

    @Test
    public void GetWorkmates2Records() {
        Initialization();
        when(workmateRef.get()).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        workmates = t.getWorkmates();
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(task.getResult()).thenReturn(querySnapshot);
        when(task.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document.toObject(Workmate.class)).thenReturn(workmate1);
        when(document2.toObject(Workmate.class)).thenReturn(workmate2);
        eventGetListenerCaptor.getValue().onComplete(task);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(2, workmates.getValue().size());
        assertNotNull(workmates.getValue().get(0));
        assertEquals("vjraymon@gmail.com",workmates.getValue().get(0).getEmail() );
        assertEquals("Jean-Raymond Vieux",workmates.getValue().get(0).getName() );
        assertNotNull(workmates.getValue().get(1));
        assertEquals("vagnes@gmail.com",workmates.getValue().get(1).getEmail() );
        assertEquals("Agnes Vieux",workmates.getValue().get(1).getName() );
    }

    @Captor
    ArgumentCaptor<OnCompleteListener<QuerySnapshot>> eventGetListenerCaptor2;
    @Captor
    ArgumentCaptor<OnFailureListener> eventGetFailureListenerCaptor2;
    @Captor
    ArgumentCaptor<Workmate> myselfCaptor;
    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> eventSetListenerCaptor;
    @Captor
    ArgumentCaptor<OnFailureListener> eventSetFailureCaptor;
    @Mock
    Task<QuerySnapshot> taskAdd;
    Task<Void> taskSet;
    @Mock
    DocumentReference documentReferenceSet;

    @Test
    public void AddWorkmateNotAlreadyRegistered() {

        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd);
        when(taskAdd.addOnCompleteListener(any())).thenReturn(taskAdd);
        when(taskAdd.addOnFailureListener(any())).thenReturn(taskAdd);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd.isSuccessful()).thenReturn(true);
        when(taskAdd.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor2.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor2.capture());
        assertNotNull(eventGetListenerCaptor2.getValue());
        assertNotNull(eventGetFailureListenerCaptor2.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReferenceSet);
        when(documentReferenceSet.set(myself)).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor2.getValue().onComplete(taskAdd);

        verify(documentReferenceSet).set(myselfCaptor.capture());
        verify(taskUpdate).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventSetFailureCaptor.getValue());
        assertNotNull(myselfCaptor.getValue());
        assertEquals("vjraymon@gmail.com", myselfCaptor.getValue().getEmail());
        assertEquals("Jean-Raymond Vieux", myselfCaptor.getValue().getName());
        eventSetListenerCaptor.getValue().onSuccess(null);

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(1, workmates.getValue().size());
        assertEquals("vjraymon@gmail.com", workmates.getValue().get(0).getEmail());
        assertEquals("Jean-Raymond Vieux", workmates.getValue().get(0).getName());
    }

    @Test
    public void AddWorkmateException2() {

        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd);
        when(taskAdd.addOnCompleteListener(any())).thenReturn(taskAdd);
        when(taskAdd.addOnFailureListener(any())).thenReturn(taskAdd);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd.isSuccessful()).thenReturn(true);
        when(taskAdd.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor2.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor2.capture());
        assertNotNull(eventGetListenerCaptor2.getValue());
        assertNotNull(eventGetFailureListenerCaptor2.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReferenceSet);
        when(documentReferenceSet.set(myself)).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor2.getValue().onComplete(taskAdd);

        verify(documentReferenceSet).set(myselfCaptor.capture());
        verify(taskUpdate).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventSetFailureCaptor.getValue());
        assertNotNull(myselfCaptor.getValue());
        assertEquals("vjraymon@gmail.com", myselfCaptor.getValue().getEmail());
        assertEquals("Jean-Raymond Vieux", myselfCaptor.getValue().getName());
        eventSetFailureCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskAdd2;

    @Test
    public void AddWorkmateAlreadyRegistered() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd2);
        when(taskAdd2.addOnCompleteListener(any())).thenReturn(taskAdd2);
        when(taskAdd2.addOnFailureListener(any())).thenReturn(taskAdd2);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd2.isSuccessful()).thenReturn(true);
        when(taskAdd2.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document.toObject(Workmate.class)).thenReturn(workmate1);
        when(document2.toObject(Workmate.class)).thenReturn(workmate2);
        verify(taskAdd2).addOnCompleteListener(eventGetListenerCaptor2.capture());
        verify(taskAdd2).addOnFailureListener(eventGetFailureListenerCaptor2.capture());
        assertNotNull(eventGetListenerCaptor2.getValue());
        assertNotNull(eventGetFailureListenerCaptor2.getValue());
        eventGetListenerCaptor2.getValue().onComplete(taskAdd2);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(2, workmates.getValue().size());
        assertNotNull(workmates.getValue().get(0));
        assertEquals("vjraymon@gmail.com",workmates.getValue().get(0).getEmail() );
        assertEquals("Jean-Raymond Vieux",workmates.getValue().get(0).getName() );
        assertNotNull(workmates.getValue().get(1));
        assertEquals("vagnes@gmail.com",workmates.getValue().get(1).getEmail() );
        assertEquals("Agnes Vieux",workmates.getValue().get(1).getName() );
    }

    @Test
    public void AddWorkmateError() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd2);
        when(taskAdd2.addOnCompleteListener(any())).thenReturn(taskAdd2);
        when(taskAdd2.addOnFailureListener(any())).thenReturn(taskAdd2);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd2.isSuccessful()).thenReturn(false);
        verify(taskAdd2).addOnCompleteListener(eventGetListenerCaptor2.capture());
        verify(taskAdd2).addOnFailureListener(eventGetFailureListenerCaptor2.capture());
        assertNotNull(eventGetListenerCaptor2.getValue());
        assertNotNull(eventGetFailureListenerCaptor2.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskAdd2);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskAddException1;

    @Test
    public void AddWorkmateException1() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAddException1);
        when(taskAddException1.addOnCompleteListener(any())).thenReturn(taskAddException1);
        when(taskAddException1.addOnFailureListener(any())).thenReturn(taskAddException1);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        verify(taskAddException1).addOnCompleteListener(eventGetListenerCaptor2.capture());
        verify(taskAddException1).addOnFailureListener(eventGetFailureListenerCaptor2.capture());
        assertNotNull(eventGetListenerCaptor2.getValue());
        assertNotNull(eventGetFailureListenerCaptor2.getValue());
        eventGetFailureListenerCaptor2.getValue().onFailure(new Exception("Exception"));
        assertNotNull(workmates);
        assertNull(workmates.getValue()); // reset
    }

    @Mock
    Task<QuerySnapshot> taskSetException;

    @Test
    public void SetRestaurantException() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSetException);
        when(taskSetException.addOnCompleteListener(any())).thenReturn(taskSetException);
        when(taskSetException.addOnFailureListener(any())).thenReturn(taskSetException);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Restaurant restaurant = new Restaurant(
                "IdGoogleMap",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10,10),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        );
        t.setRestaurant(myself, restaurant);
        verify(taskSetException).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSetException).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskSetError;
    @Test
    public void SetRestaurantError() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSetError);
        when(taskSetError.addOnCompleteListener(any())).thenReturn(taskSetError);
        when(taskSetError.addOnFailureListener(any())).thenReturn(taskSetError);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Restaurant restaurant = new Restaurant(
                "IdGoogleMap",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10,10),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        );
        t.setRestaurant(myself, restaurant);
        verify(taskSetError).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSetError).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSetError.isSuccessful()).thenReturn(false);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskSetError);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskSetMyselfNotExisting;

    @Test
    public void SetRestaurantMyselfNotAlreadyExisting() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSetMyselfNotExisting);
        when(taskSetMyselfNotExisting.addOnCompleteListener(any())).thenReturn(taskSetMyselfNotExisting);
        when(taskSetMyselfNotExisting.addOnFailureListener(any())).thenReturn(taskSetMyselfNotExisting);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Restaurant restaurant = new Restaurant(
                "IdGoogleMap",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10,10),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        );
        t.setRestaurant(myself, restaurant);
        verify(taskSetMyselfNotExisting).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSetMyselfNotExisting).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSetMyselfNotExisting.isSuccessful()).thenReturn(true);
        when(taskSetMyselfNotExisting.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskSetMyselfNotExisting);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskSetMyselfExisting;

    @Test
    public void SetRestaurantMyselfAlreadyExisting() {
        GetWorkmates2Records();
        when(workmateRef.get()).thenReturn(taskSetMyselfExisting);
        when(taskSetMyselfExisting.addOnCompleteListener(any())).thenReturn(taskSetMyselfExisting);
        when(taskSetMyselfExisting.addOnFailureListener(any())).thenReturn(taskSetMyselfExisting);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Restaurant restaurant = new Restaurant(
                "IdGoogleMap",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10,10),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        );
        t.setRestaurant(myself, restaurant);

        verify(taskSetMyselfExisting).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSetMyselfExisting).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSetMyselfExisting.isSuccessful()).thenReturn(true);
        when(taskSetMyselfExisting.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document.toObject(Workmate.class)).thenReturn(workmate1);
        when(document2.toObject(Workmate.class)).thenReturn(workmate2);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.update("idRestaurant", "IdGoogleMap")).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskSetMyselfExisting);

        verify(taskUpdate).addOnSuccessListener(eventupdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventupdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventupdateListenerCaptor.getValue().onSuccess(null);
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(2, workmates.getValue().size()); // unchanged
    }

    @Mock
    DocumentReference documentReference;

    @Mock
    Task<Void> taskUpdate;
    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> eventupdateListenerCaptor;
    @Test
    public void UpdateIdRestaurant() {
        Initialization();
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.update("idRestaurant", "IdGoogleMap")).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.updateIdRestaurant(myself, "IdGoogleMap");
        verify(taskUpdate).addOnSuccessListener(eventupdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventupdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventupdateListenerCaptor.getValue().onSuccess(null);
    }

    @Test
    public void UpdateIdRestaurantException() {
        Initialization();
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.update("idRestaurant", "IdGoogleMap")).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.updateIdRestaurant(myself, "IdGoogleMap");
        verify(taskUpdate).addOnSuccessListener(eventupdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventupdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));
    }

}
