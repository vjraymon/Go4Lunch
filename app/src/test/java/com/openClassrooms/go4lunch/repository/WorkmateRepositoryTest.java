//TODO rename the package
package com.openClassrooms.go4lunch.repository;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.openClassrooms.go4lunch.model.Restaurant;
import com.openClassrooms.go4lunch.model.Workmate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WorkmateRepositoryTest {
    WorkmateRepository t;

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore fireStore;
    @Mock
    CollectionReference workmateRef;

    @Captor
    ArgumentCaptor<EventListener<QuerySnapshot>> eventSnapshotListenerCaptor;

    @Mock
    QuerySnapshot querySnapshot;

    List<DocumentSnapshot> listDocumentSnapshot = new ArrayList<>();

    @Mock
    DocumentSnapshot document1, document2;

    @Test
    public void Initialization() {
        when(fireStore.collection("workmates")).thenReturn(workmateRef);
        when(workmateRef.addSnapshotListener(any())).thenReturn(null);
        t = new WorkmateRepository(fireStore);
        verify(workmateRef).addSnapshotListener(eventSnapshotListenerCaptor.capture());
        assertNotNull(eventSnapshotListenerCaptor.getValue());
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        eventSnapshotListenerCaptor.getValue().onEvent(querySnapshot, null);
    }

    @Test
    public void DbModification() {
        GetWorkmatesEmpty();
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        when(document1.toObject(Workmate.class)).thenReturn(workmate1);
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
        listDocumentSnapshot.add(document1);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        when(document1.toObject(Workmate.class)).thenReturn(myself);
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
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document1.toObject(Workmate.class)).thenReturn(workmate1);
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
    ArgumentCaptor<Workmate> myselfCaptor;
    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> eventSetListenerCaptor;
    @Captor
    ArgumentCaptor<OnFailureListener> eventSetFailureCaptor;
    @Mock
    Task<QuerySnapshot> taskAdd;

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
        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.set(myself)).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskAdd);

        verify(documentReference).set(myselfCaptor.capture());
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
        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.set(myself)).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskAdd);

        verify(documentReference).set(myselfCaptor.capture());
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

    @Test
    public void AddWorkmateAlreadyRegistered() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd);
        when(taskAdd.addOnCompleteListener(any())).thenReturn(taskAdd);
        when(taskAdd.addOnFailureListener(any())).thenReturn(taskAdd);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd.isSuccessful()).thenReturn(true);
        when(taskAdd.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document1.toObject(Workmate.class)).thenReturn(workmate1);
        when(document2.toObject(Workmate.class)).thenReturn(workmate2);
        verify(task).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(task).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskAdd);

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
        when(workmateRef.get()).thenReturn(taskAdd);
        when(taskAdd.addOnCompleteListener(any())).thenReturn(taskAdd);
        when(taskAdd.addOnFailureListener(any())).thenReturn(taskAdd);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        when(taskAdd.isSuccessful()).thenReturn(false);
        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskAdd);

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Test
    public void AddWorkmateException1() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskAdd);
        when(taskAdd.addOnCompleteListener(any())).thenReturn(taskAdd);
        when(taskAdd.addOnFailureListener(any())).thenReturn(taskAdd);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.addWorkmate(myself);

        verify(taskAdd).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAdd).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(workmates);
        assertNull(workmates.getValue()); // reset
    }

    @Mock
    Task<QuerySnapshot> taskSet;

    @Test
    public void SetRestaurantException() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSet);
        when(taskSet.addOnCompleteListener(any())).thenReturn(taskSet);
        when(taskSet.addOnFailureListener(any())).thenReturn(taskSet);
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

        verify(taskSet).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSet).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Test
    public void SetRestaurantError() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSet);
        when(taskSet.addOnCompleteListener(any())).thenReturn(taskSet);
        when(taskSet.addOnFailureListener(any())).thenReturn(taskSet);
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

        verify(taskSet).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSet).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSet.isSuccessful()).thenReturn(false);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskSet);

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Test
    public void SetRestaurantMyselfNotAlreadyExisting() {
        GetWorkmatesEmpty();
        when(workmateRef.get()).thenReturn(taskSet);
        when(taskSet.addOnCompleteListener(any())).thenReturn(taskSet);
        when(taskSet.addOnFailureListener(any())).thenReturn(taskSet);
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

        verify(taskSet).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSet).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSet.isSuccessful()).thenReturn(true);
        when(taskSet.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskSet);

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(0, workmates.getValue().size()); // unchanged
    }

    @Mock
    DocumentReference documentReference;

    @Mock
    Task<Void> taskUpdate;
    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> eventUpdateListenerCaptor;

    @Test
    public void SetRestaurantMyselfAlreadyExisting() {
        GetWorkmates2Records();
        when(workmateRef.get()).thenReturn(taskSet);
        when(taskSet.addOnCompleteListener(any())).thenReturn(taskSet);
        when(taskSet.addOnFailureListener(any())).thenReturn(taskSet);
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

        verify(taskSet).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskSet).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskSet.isSuccessful()).thenReturn(true);
        when(taskSet.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        Workmate workmate1 = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        Workmate workmate2 = new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "Chez Tintin");
        when(document1.toObject(Workmate.class)).thenReturn(workmate1);
        when(document2.toObject(Workmate.class)).thenReturn(workmate2);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.update("idRestaurant", "IdGoogleMap")).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskSet);

        verify(taskUpdate).addOnSuccessListener(eventUpdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventUpdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventUpdateListenerCaptor.getValue().onSuccess(null);

        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(2, workmates.getValue().size()); // unchanged
    }

    @Test
    public void UpdateIdRestaurant() {
        Initialization();
        when(workmateRef.document("vjraymon@gmail.com")).thenReturn(documentReference);
        when(documentReference.update("idRestaurant", "IdGoogleMap")).thenReturn(taskUpdate);
        when(taskUpdate.addOnSuccessListener(any())).thenReturn(taskUpdate);
        when(taskUpdate.addOnFailureListener(any())).thenReturn(taskUpdate);
        Workmate myself = new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null);
        t.updateIdRestaurant(myself, "IdGoogleMap");

        verify(taskUpdate).addOnSuccessListener(eventUpdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventUpdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventUpdateListenerCaptor.getValue().onSuccess(null);
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

        verify(taskUpdate).addOnSuccessListener(eventUpdateListenerCaptor.capture());
        verify(taskUpdate).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventUpdateListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));
    }

}
