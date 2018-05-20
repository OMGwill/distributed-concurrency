//Will Luttmann
//note: may need to run the unsynchronized program a few times to 
// see that the ID numbers get duplictaed ex. ...1030 1040 1040 1050...

class ClientInfo 
{
    private int _clientId;
    private String _clientName;

    public ClientInfo(int id, String name) 
    {
        setClientId(id);
        setClientName(name);
    }

    public int getClientId() 
    {
        return (_clientId);
    }

    public void setClientId(int id) 
    {
        _clientId = id;
    }

    public String getClientName() 
    {
        return (_clientName);
    }

    public void setClientName(String name) 
    {
        _clientName = name;
    }
}

class ClientList 
{
    private int _nextID;
    private static final int MAX_NUM_VALUES = 30;
    private ClientInfo[] _list = new ClientInfo[MAX_NUM_VALUES];
    private int _numValues;

    public ClientList() 
    {
        _nextID = 1000;
        _numValues = 0;
    }
	
	public void sleepAWhile (int min) {
        try {
            Thread.sleep(min + (int)(Math.random()*500));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

    public synchronized //sychronized fixes race condition for names and ID and 'no client info'
		void addClient(String name) //needs exclusive access
    {
		int temp; // need temp to reveal race condition when updating _nextID

        if (_numValues < MAX_NUM_VALUES) 
        {
		    _list[_numValues] = new ClientInfo(_nextID, name);
			 
			sleepAWhile(10); //sleep to reveal problems with no client info
							 // and name order
            _numValues++;
			
            temp = _nextID;
			sleepAWhile(10);   //sleep to reveal problem with ID increment reaching max (1100),
							   // repeating ID numbers ...1030 1040 1040 1050..., and name order
			temp = temp + 10;
			_nextID = temp;
        }
        else
        {
           System.out.println("Exceeded maximum number of clients " + MAX_NUM_VALUES);
        }
    }

    public void showClients() 
    {
        for (int i=0; i<_numValues; i++) 
        {
            if (_list[i]==null)
                System.out.println("No ClientInfo object.");
            else
				System.out.format("Client %s has id %d\n", _list[i].getClientName(),
                    _list[i].getClientId());   
        }
    }
}

class ClientAdder implements Runnable 
{
    private String[] _names;
    private ClientList _clientList;
    
    public ClientAdder (ClientList l, String[] names) //uses shared object
    {
        _clientList = l;
        _names = names;
    }
	
	public void sleepAWhile (int min) {
        try {
            Thread.sleep(min + (int)(Math.random()*500));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
    public void run() 
    {
		
        // Use _names to create clients in _clientList
        for (int i=0; i < _names.length; i++) 
        {
			_clientList.addClient(_names[i]); //shared resource needs exclusive acess
		}
	}
}

public class Homework1 
{

    public static void main(String[] args) 
            throws InterruptedException 
    {
        ClientList clientList = new ClientList();// shared object
        
        // Create a thread that will put first set of clients in the clientList
        String[] firstSetOfNames = {"Albert", "Bernice", "Charlotte", "Dedrick", "Edwin"};
        ClientAdder firstClients = new ClientAdder(clientList, firstSetOfNames);
        Thread thread1 = new Thread (firstClients);

        // Create a thread that will put last set of clients in the clientList
        String[] lastSetOfNames = {"Ursula", "Veronica", "Willard", "Xavier", "Yolanda", "Zachary"};
        ClientAdder lastClients = new ClientAdder(clientList, lastSetOfNames);
        Thread thread2 = new Thread (lastClients);

        // Start both threads
        thread1.start();
        Thread.sleep(1000); // the sleep helps mask bad behavior
        thread2.start();

        // Stop both threads
        thread2.join();
        thread1.join();

        // Print the clientList
        System.out.println("Final list:");
        clientList.showClients();
        System.exit(0);
    }  
}