//TODO rename the package
package com.openclassrooms.go4lunch.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

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
import com.openclassrooms.go4lunch.model.RestaurantLike;

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
public class RestaurantLikeRepositoryTest {
    RestaurantLikeRepository t;

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firestore;
    @Mock
    CollectionReference restaurantLikeRef;

    @Captor
    ArgumentCaptor<EventListener<QuerySnapshot>> eventSnapshotListenerCaptor;

//    ListenerRegistration listenerRegistration;

    @Mock
    QuerySnapshot querySnapshot;

    List<DocumentSnapshot> listDocumentSnapshot = new ArrayList<>();

    @Test
    public void Initialization() {
        when(firestore.collection("restaurants")).thenReturn(restaurantLikeRef);
        // the listenerRegistratuin is never removed
        when(restaurantLikeRef.addSnapshotListener(any())).thenReturn(null);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
//            t = RestaurantLikeRepository.getRestaurantLikeRepository(firestore);
        t = new RestaurantLikeRepository(firestore);
        verify(restaurantLikeRef).addSnapshotListener(eventSnapshotListenerCaptor.capture());
        assertNotNull(eventSnapshotListenerCaptor.getValue());
        eventSnapshotListenerCaptor.getValue().onEvent(querySnapshot, null);
    }

    @Mock
    DocumentSnapshot document1, document2;

    @Test
    public void DbModification() {
        GetRestaurantLikesEmpty();
//        myApplication = new Application();
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "LaScala", 1);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        eventSnapshotListenerCaptor.getValue().onEvent(querySnapshot, null);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(1, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com", restaurantLikes.getValue().get(0).getId());
        assertEquals("LaScala", restaurantLikes.getValue().get(0).getName());

    }

    @Captor
    ArgumentCaptor<OnCompleteListener<QuerySnapshot>> eventGetListenerCaptor;
    @Captor
    ArgumentCaptor<OnFailureListener> eventGetFailureListenerCaptor;

    LiveData<List<RestaurantLike>> restaurantLikes;

    @Mock
    Task<QuerySnapshot> taskGetRestaurantLikesError;

    @Test
    public void GetRestaurantLikesError() {
        Initialization();
        when(restaurantLikeRef.get()).thenReturn(taskGetRestaurantLikesError);
        when(taskGetRestaurantLikesError.addOnCompleteListener(any())).thenReturn(taskGetRestaurantLikesError);
        when(taskGetRestaurantLikesError.addOnFailureListener(any())).thenReturn(taskGetRestaurantLikesError);
        restaurantLikes = t.getRestaurantLikes();

        verify(taskGetRestaurantLikesError).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskGetRestaurantLikesError).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(taskGetRestaurantLikesError.isSuccessful()).thenReturn(false);
        eventGetListenerCaptor.getValue().onComplete(taskGetRestaurantLikesError);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertTrue(restaurantLikes.getValue().isEmpty());
    }

    @Mock
    Task<QuerySnapshot> taskGetRestaurantLikesException;

    @Test
    public void GetRestaurantLikesException() {
        Initialization();
        when(restaurantLikeRef.get()).thenReturn(taskGetRestaurantLikesException);
        when(taskGetRestaurantLikesException.addOnCompleteListener(any())).thenReturn(taskGetRestaurantLikesException);
        when(taskGetRestaurantLikesException.addOnFailureListener(any())).thenReturn(taskGetRestaurantLikesException);
        restaurantLikes = t.getRestaurantLikes();

        verify(taskGetRestaurantLikesException).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskGetRestaurantLikesException).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(restaurantLikes);
        assertNull(restaurantLikes.getValue());
    }

    @Mock
    Task<QuerySnapshot> taskGetRestaurantLikesEmpty;

    @Test
    public void GetRestaurantLikesEmpty() {
        Initialization();
        when(restaurantLikeRef.get()).thenReturn(taskGetRestaurantLikesEmpty);
        when(taskGetRestaurantLikesEmpty.addOnCompleteListener(any())).thenReturn(taskGetRestaurantLikesEmpty);
        when(taskGetRestaurantLikesEmpty.addOnFailureListener(any())).thenReturn(taskGetRestaurantLikesEmpty);
        restaurantLikes = t.getRestaurantLikes();

        verify(taskGetRestaurantLikesEmpty).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskGetRestaurantLikesEmpty).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(taskGetRestaurantLikesEmpty.getResult()).thenReturn(querySnapshot);
        when(taskGetRestaurantLikesEmpty.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        eventGetListenerCaptor.getValue().onComplete(taskGetRestaurantLikesEmpty);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertTrue(restaurantLikes.getValue().isEmpty());
    }

    @Mock
    Task<QuerySnapshot> taskGetRestaurantLikes1Record;

    @Test
    public void GetRestaurantLikes1Record() {
        Initialization();
        when(restaurantLikeRef.get()).thenReturn(taskGetRestaurantLikes1Record);
        when(taskGetRestaurantLikes1Record.addOnCompleteListener(any())).thenReturn(taskGetRestaurantLikes1Record);
        when(taskGetRestaurantLikes1Record.addOnFailureListener(any())).thenReturn(taskGetRestaurantLikes1Record);
        restaurantLikes = t.getRestaurantLikes();

        verify(taskGetRestaurantLikes1Record).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskGetRestaurantLikes1Record).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(taskGetRestaurantLikes1Record.getResult()).thenReturn(querySnapshot);
        when(taskGetRestaurantLikes1Record.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 1);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        eventGetListenerCaptor.getValue().onComplete(taskGetRestaurantLikes1Record);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(1, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com", restaurantLikes.getValue().get(0).getId() );
        assertEquals("La Scala", restaurantLikes.getValue().get(0).getName() );
        assertEquals(1, restaurantLikes.getValue().get(0).getLike() );
    }

    @Mock
    Task<QuerySnapshot> taskGetRestaurantLikes2Records;

    @Test
    public void GetRestaurantLikes2Records() {
        Initialization();
        when(restaurantLikeRef.get()).thenReturn(taskGetRestaurantLikes2Records);
        when(taskGetRestaurantLikes2Records.addOnCompleteListener(any())).thenReturn(taskGetRestaurantLikes2Records);
        when(taskGetRestaurantLikes2Records.addOnFailureListener(any())).thenReturn(taskGetRestaurantLikes2Records);
        restaurantLikes = t.getRestaurantLikes();

        verify(taskGetRestaurantLikes2Records).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskGetRestaurantLikes2Records).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(taskGetRestaurantLikes2Records.getResult()).thenReturn(querySnapshot);
        when(taskGetRestaurantLikes2Records.isSuccessful()).thenReturn(true);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 1);
        RestaurantLike restaurantLike2 = new RestaurantLike("LaScalaReferencevagnes@gmail.com", "La Scala", 2);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        when(document2.toObject(RestaurantLike.class)).thenReturn(restaurantLike2);
        eventGetListenerCaptor.getValue().onComplete(taskGetRestaurantLikes2Records);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(2, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com",restaurantLikes.getValue().get(0).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(0).getName() );
        assertEquals(1,restaurantLikes.getValue().get(0).getLike() );
        assertNotNull(restaurantLikes.getValue().get(1));
        assertEquals("LaScalaReferencevagnes@gmail.com",restaurantLikes.getValue().get(1).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(1).getName() );
        assertEquals(2,restaurantLikes.getValue().get(1).getLike() );
    }

    @Mock
    Task<QuerySnapshot> taskAddException1;

    @Test
    public void AddRestaurantLikeException1() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskAddException1);
        when(taskAddException1.addOnCompleteListener(any())).thenReturn(taskAddException1);
        when(taskAddException1.addOnFailureListener(any())).thenReturn(taskAddException1);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.addRestaurantLike(newLike);

        verify(taskAddException1).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAddException1).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(restaurantLikes);
        assertNull(restaurantLikes.getValue()); // reset
    }

    @Mock
    Task<QuerySnapshot> taskAddError;

    @Test
    public void AddRestaurantLikeError() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskAddError);
        when(taskAddError.addOnCompleteListener(any())).thenReturn(taskAddError);
        when(taskAddError.addOnFailureListener(any())).thenReturn(taskAddError);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.addRestaurantLike(newLike);

        when(taskAddError.isSuccessful()).thenReturn(false);
        verify(taskAddError).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAddError).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskAddError);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskAlreadyRegistered;

    @Test
    public void AddRestaurantLikeAlreadyRegistered() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskAlreadyRegistered);
        when(taskAlreadyRegistered.addOnCompleteListener(any())).thenReturn(taskAlreadyRegistered);
        when(taskAlreadyRegistered.addOnFailureListener(any())).thenReturn(taskAlreadyRegistered);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.addRestaurantLike(newLike);

        when(taskAlreadyRegistered.isSuccessful()).thenReturn(true);
        when(taskAlreadyRegistered.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 1);
        RestaurantLike restaurantLike2 = new RestaurantLike("LaScalaReferencevagnes@gmail.com", "La Scala", 2);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        when(document2.toObject(RestaurantLike.class)).thenReturn(restaurantLike2);
        verify(taskAlreadyRegistered).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAlreadyRegistered).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskAlreadyRegistered);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(2, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com",restaurantLikes.getValue().get(0).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(0).getName() );
        assertEquals(1,restaurantLikes.getValue().get(0).getLike() );
        assertNotNull(restaurantLikes.getValue().get(1));
        assertEquals("LaScalaReferencevagnes@gmail.com",restaurantLikes.getValue().get(1).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(1).getName() );
        assertEquals(2,restaurantLikes.getValue().get(1).getLike() );
    }

    @Mock
    DocumentReference documentReferenceAdd;
    @Captor
    ArgumentCaptor<RestaurantLike> newLikeCaptor;
    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> eventSetListenerCaptor;
    @Captor
    ArgumentCaptor<OnFailureListener> eventSetFailureCaptor;

    @Mock
    Task<QuerySnapshot> taskAddException2;
    @Mock
    Task<Void> taskAddException2Update;

    @Test
    public void AddRestaurantLikeException2() {

        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskAddException2);
        when(taskAddException2.addOnCompleteListener(any())).thenReturn(taskAddException2);
        when(taskAddException2.addOnFailureListener(any())).thenReturn(taskAddException2);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.addRestaurantLike(newLike);

        when(taskAddException2.isSuccessful()).thenReturn(true);
        when(taskAddException2.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        verify(taskAddException2).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAddException2).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(restaurantLikeRef.document("LaScalaReferencevjraymon@gmail.com")).thenReturn(documentReferenceAdd);
        when(documentReferenceAdd.set(newLike)).thenReturn(taskAddException2Update);
        when(taskAddException2Update.addOnSuccessListener(any())).thenReturn(taskAddException2Update);
        when(taskAddException2Update.addOnFailureListener(any())).thenReturn(taskAddException2Update);
        eventGetListenerCaptor.getValue().onComplete(taskAddException2);

        verify(documentReferenceAdd).set(newLikeCaptor.capture());
        verify(taskAddException2Update).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskAddException2Update).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventSetFailureCaptor.getValue());
        assertNotNull(newLikeCaptor.getValue());
        assertEquals("LaScalaReferencevjraymon@gmail.com", newLikeCaptor.getValue().getId());
        assertEquals("La Scala", newLikeCaptor.getValue().getName());
        assertEquals(2, newLikeCaptor.getValue().getLike());
        eventSetFailureCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskAddNotAlreadyRegistered;
    @Mock
    Task<Void> taskAddNotAlreadyRegisteredUpdate;

    @Test
    public void AddRestaurantLikeNotAlreadyRegistered() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskAddNotAlreadyRegistered);
        when(taskAddNotAlreadyRegistered.addOnCompleteListener(any())).thenReturn(taskAddNotAlreadyRegistered);
        when(taskAddNotAlreadyRegistered.addOnFailureListener(any())).thenReturn(taskAddNotAlreadyRegistered);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.addRestaurantLike(newLike);

        when(taskAddNotAlreadyRegistered.isSuccessful()).thenReturn(true);
        when(taskAddNotAlreadyRegistered.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        verify(taskAddNotAlreadyRegistered).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskAddNotAlreadyRegistered).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(restaurantLikeRef.document("LaScalaReferencevjraymon@gmail.com")).thenReturn(documentReferenceAdd);
        when(documentReferenceAdd.set(newLike)).thenReturn(taskAddNotAlreadyRegisteredUpdate);
        when(taskAddNotAlreadyRegisteredUpdate.addOnSuccessListener(any())).thenReturn(taskAddNotAlreadyRegisteredUpdate);
        when(taskAddNotAlreadyRegisteredUpdate.addOnFailureListener(any())).thenReturn(taskAddNotAlreadyRegisteredUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskAddNotAlreadyRegistered);

        verify(documentReferenceAdd).set(newLikeCaptor.capture());
        verify(taskAddNotAlreadyRegisteredUpdate).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskAddNotAlreadyRegisteredUpdate).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventSetFailureCaptor.getValue());
        assertNotNull(newLikeCaptor.getValue());
        assertEquals("LaScalaReferencevjraymon@gmail.com", newLikeCaptor.getValue().getId());
        assertEquals("La Scala", newLikeCaptor.getValue().getName());
        assertEquals(2, newLikeCaptor.getValue().getLike());
        eventSetListenerCaptor.getValue().onSuccess(null);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(1, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com",restaurantLikes.getValue().get(0).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(0).getName() );
        assertEquals(2,restaurantLikes.getValue().get(0).getLike() );
    }

    @Mock
    Task<QuerySnapshot> taskUpdateLikeException1;

    @Test
    public void UpdateLikeException1() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskUpdateLikeException1);
        when(taskUpdateLikeException1.addOnCompleteListener(any())).thenReturn(taskUpdateLikeException1);
        when(taskUpdateLikeException1.addOnFailureListener(any())).thenReturn(taskUpdateLikeException1);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.updateLike(newLike, 1);

        verify(taskUpdateLikeException1).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskUpdateLikeException1).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetFailureListenerCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskUpdateLikeError;

    @Test
    public void UpdateLikeError() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskUpdateLikeError);
        when(taskUpdateLikeError.addOnCompleteListener(any())).thenReturn(taskUpdateLikeError);
        when(taskUpdateLikeError.addOnFailureListener(any())).thenReturn(taskUpdateLikeError);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.updateLike(newLike, 1);

        verify(taskUpdateLikeError).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskUpdateLikeError).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskUpdateLikeError.isSuccessful()).thenReturn(false);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskUpdateLikeError);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskUpdateLikeNotAlreadyExisting;

    @Test
    public void UpdateLikeNotAlreadyExisting() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskUpdateLikeNotAlreadyExisting);
        when(taskUpdateLikeNotAlreadyExisting.addOnCompleteListener(any())).thenReturn(taskUpdateLikeNotAlreadyExisting);
        when(taskUpdateLikeNotAlreadyExisting.addOnFailureListener(any())).thenReturn(taskUpdateLikeNotAlreadyExisting);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.updateLike(newLike, 1);

        verify(taskUpdateLikeNotAlreadyExisting).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskUpdateLikeNotAlreadyExisting).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskUpdateLikeNotAlreadyExisting.isSuccessful()).thenReturn(true);
        when(taskUpdateLikeNotAlreadyExisting.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventGetListenerCaptor.getValue().onComplete(taskUpdateLikeNotAlreadyExisting);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    DocumentReference documentReferenceUpdate;

    @Mock
    Task<QuerySnapshot> taskUpdateLikeException2;
    @Mock
    Task<Void> taskUpdateLikeException2Update;

    @Test
    public void UpdateLikeException2() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskUpdateLikeException2);
        when(taskUpdateLikeException2.addOnCompleteListener(any())).thenReturn(taskUpdateLikeException2);
        when(taskUpdateLikeException2.addOnFailureListener(any())).thenReturn(taskUpdateLikeException2);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.updateLike(newLike, 3);

        verify(taskUpdateLikeException2).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskUpdateLikeException2).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskUpdateLikeException2.isSuccessful()).thenReturn(true);
        when(taskUpdateLikeException2.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 1);
        RestaurantLike restaurantLike2 = new RestaurantLike("LaScalaReferencevagnes@gmail.com", "La Scala", 2);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        when(document2.toObject(RestaurantLike.class)).thenReturn(restaurantLike2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(restaurantLikeRef.document("LaScalaReferencevjraymon@gmail.com")).thenReturn(documentReferenceUpdate);
        when(documentReferenceUpdate.update("like", 3)).thenReturn(taskUpdateLikeException2Update);
        when(taskUpdateLikeException2Update.addOnSuccessListener(any())).thenReturn(taskUpdateLikeException2Update);
        when(taskUpdateLikeException2Update.addOnFailureListener(any())).thenReturn(taskUpdateLikeException2Update);
        eventGetListenerCaptor.getValue().onComplete(taskUpdateLikeException2);

        verify(taskUpdateLikeException2Update).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskUpdateLikeException2Update).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventSetFailureCaptor.getValue());
        eventSetFailureCaptor.getValue().onFailure(new Exception("Exception"));

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(0, restaurantLikes.getValue().size()); // unchanged
    }

    @Mock
    Task<QuerySnapshot> taskUpdateLikeAlreadyExisting;
    @Mock
    Task<Void> taskUpdateLikeAlreadyExistingUpdate;

    @Test
    public void UpdateLikeAlreadyExisting() {
        GetRestaurantLikesEmpty();
        when(restaurantLikeRef.get()).thenReturn(taskUpdateLikeAlreadyExisting);
        when(taskUpdateLikeAlreadyExisting.addOnCompleteListener(any())).thenReturn(taskUpdateLikeAlreadyExisting);
        when(taskUpdateLikeAlreadyExisting.addOnFailureListener(any())).thenReturn(taskUpdateLikeAlreadyExisting);
        RestaurantLike newLike = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 2);
        t.updateLike(newLike, 3);


        verify(taskUpdateLikeAlreadyExisting).addOnCompleteListener(eventGetListenerCaptor.capture());
        verify(taskUpdateLikeAlreadyExisting).addOnFailureListener(eventGetFailureListenerCaptor.capture());
        when(taskUpdateLikeAlreadyExisting.isSuccessful()).thenReturn(true);
        when(taskUpdateLikeAlreadyExisting.getResult()).thenReturn(querySnapshot);
        listDocumentSnapshot = new ArrayList<>();
        listDocumentSnapshot.add(document1);
        listDocumentSnapshot.add(document2);
        RestaurantLike restaurantLike1 = new RestaurantLike("LaScalaReferencevjraymon@gmail.com", "La Scala", 1);
        RestaurantLike restaurantLike2 = new RestaurantLike("LaScalaReferencevagnes@gmail.com", "La Scala", 2);
        when(document1.toObject(RestaurantLike.class)).thenReturn(restaurantLike1);
        when(document2.toObject(RestaurantLike.class)).thenReturn(restaurantLike2);
        when(querySnapshot.getDocuments()).thenReturn(listDocumentSnapshot);
        assertNotNull(eventGetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        when(restaurantLikeRef.document("LaScalaReferencevjraymon@gmail.com")).thenReturn(documentReferenceUpdate);
        when(documentReferenceUpdate.update("like", 3)).thenReturn(taskUpdateLikeAlreadyExistingUpdate);
        when(taskUpdateLikeAlreadyExistingUpdate.addOnSuccessListener(any())).thenReturn(taskUpdateLikeAlreadyExistingUpdate);
        when(taskUpdateLikeAlreadyExistingUpdate.addOnFailureListener(any())).thenReturn(taskUpdateLikeAlreadyExistingUpdate);
        eventGetListenerCaptor.getValue().onComplete(taskUpdateLikeAlreadyExisting);

        verify(taskUpdateLikeAlreadyExistingUpdate).addOnSuccessListener(eventSetListenerCaptor.capture());
        verify(taskUpdateLikeAlreadyExistingUpdate).addOnFailureListener(eventSetFailureCaptor.capture());
        assertNotNull(eventSetListenerCaptor.getValue());
        assertNotNull(eventGetFailureListenerCaptor.getValue());
        eventSetListenerCaptor.getValue().onSuccess(null);

        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(2, restaurantLikes.getValue().size());
        assertNotNull(restaurantLikes.getValue().get(0));
        assertEquals("LaScalaReferencevjraymon@gmail.com",restaurantLikes.getValue().get(0).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(0).getName() );
        assertEquals(3,restaurantLikes.getValue().get(0).getLike() ); // changed
        assertNotNull(restaurantLikes.getValue().get(1));
        assertEquals("LaScalaReferencevagnes@gmail.com",restaurantLikes.getValue().get(1).getId() );
        assertEquals("La Scala",restaurantLikes.getValue().get(1).getName() );
        assertEquals(2,restaurantLikes.getValue().get(1).getLike() );
    }
}
