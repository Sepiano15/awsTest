package aws;

/*
* Cloud Computing
* 
* Dynamic Resource Management Tool
* using AWS Java SDK Library
* 
*/
import java.util.Iterator;
import java.util.Scanner;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Tag;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.jcraft.jsch.*;
import java.io.ByteArrayOutputStream;

public class awsTest {

	static AmazonEC2      ec2;
	private static String privateKey = "";

	private static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
					"Please make sure that your credentials file is at the correct " +
					"location (~/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
			.withCredentials(credentialsProvider)
			.withRegion("ap-northeast-2")	/* check the region at AWS console */
			.build();
	}

	public static void main(String[] args) throws Exception {

		init();

		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		
		while(true)
		{
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("------------------------------------------------------------");
			System.out.println("           Amazon AWS Control Panel using SDK               ");
			System.out.println("------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones        ");
			System.out.println("  3. start instance by id         4. available regions      ");
			System.out.println("  5. stop instance by id          6. create instance        ");
			System.out.println("  7. reboot instance              8. list images            ");
			System.out.println("  9. show condor_status           10. start all instance    ");
			System.out.println("  11. stop all instance           12. start instance by name");
			System.out.println("  13. stop instance by name       14. send file to instance ");
			System.out.println("  15. run C program               16. get ssh link          ");
			System.out.println("  17. show running instance       99. quit                  ");
			System.out.println("------------------------------------------------------------");
			
			System.out.print("Enter an integer: ");
			
			if(menu.hasNextInt()){
				number = menu.nextInt();
				}else {
					System.out.println("concentration!");
					break;
				}
			

			String instance_id = "";
			String instance_name = "";
			String file_name = "";
			String dest_instance_name = "";

			switch(number) {
			case 1: 
				listInstances();
				break;
				
			case 2: 
				availableZones();
				break;
				
			case 3: 
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();
				
				if(!instance_id.isBlank()) 
					startInstance(instance_id);
				break;

			case 4: 
				availableRegions();
				break;

			case 5: 
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();
				
				if(!instance_id.isBlank()) 
					stopInstance(instance_id);
				break;

			case 6: 
				System.out.print("Enter ami id: ");
				String ami_id = "";
				if(id_string.hasNext())
					ami_id = id_string.nextLine();
				
				if(!ami_id.isBlank()) 
					createInstance(ami_id);
				break;

			case 7: 
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();
				
				if(!instance_id.isBlank()) 
					rebootInstance(instance_id);
				break;

			case 8: 
				listImages();
				break;
			case 9:
				sshConnect();
				break;
			case 10:
				startAll();
				break;
			case 11:
				stopAll();
				break;
			case 12: 
				System.out.print("Enter instance name: ");
				if(id_string.hasNext())
					instance_name = id_string.nextLine();
				
				if(!instance_name.isBlank()) 
					startInstance_name(instance_name);
				break;
			case 13: 
				System.out.print("Enter instance name: ");
				if(id_string.hasNext())
					instance_name = id_string.nextLine();
				
				if(!instance_name.isBlank()) 
					stopInstance_name(instance_name);
				break;
			case 14:
				System.out.print("Enter file name: ");
				if(id_string.hasNext())
					file_name = id_string.nextLine();
				
				if(!file_name.isBlank()) {
					System.out.print("Enter destination instance name: ");
					if(id_string.hasNext())
						dest_instance_name = id_string.nextLine();
					
					if(!dest_instance_name.isBlank()) 
					sendFile(file_name, dest_instance_name);
				}
				break;
			case 15:
				System.out.print("Enter file name: ");
				if(id_string.hasNext())
					file_name = id_string.nextLine();
				
				if(!file_name.isBlank()) {
					System.out.print("Enter destination instance name: ");
					if(id_string.hasNext())
						dest_instance_name = id_string.nextLine();
					
					if(!dest_instance_name.isBlank()) 
					sendFile(file_name, dest_instance_name);
				}
				runProgram(file_name, dest_instance_name);
				break;
			case 16:
				System.out.print("Enter instance name: ");
				if(id_string.hasNext())
					instance_name = id_string.nextLine();
				
				if(!instance_name.isBlank()) 
					getsshlink(instance_name);
				break;
			case 17:
				showRunningInstance();
				break;
			case 99: 
				System.out.println("bye!");
				menu.close();
				id_string.close();
				return;
			default: System.out.println("concentration!");
			}

		}
		
	}

	public static void showRunningInstance(){
		 AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        //Create the Filter to use to find running instances
        Filter filter = new Filter("instance-state-name");
        filter.withValues("running");

        //Create a DescribeInstancesRequest
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withFilters(filter);

        // Find the running instances
        DescribeInstancesResult response = ec2.describeInstances(request);
        System.out.println("\n[Running Instance Information]");
        System.out.println("------------------------------------------------------------\n");

        for (Reservation reservation : response.getReservations()){

            for (Instance instance : reservation.getInstances()) {
            	String search_name = null;
				if (instance.getTags() != null) {
		            Tag tagName = instance.getTags().stream()
                        .filter(o -> o.getKey().equals("Name"))
                        .findFirst()
                        .orElse(new Tag("Name", "name not found"));

	                search_name = tagName.getValue();
		        }

                //Print out the results
                System.out.printf(
                        "name %s, \n" +
                        "id %s, \n" +
                        "AMI %s, \n" +
                        "type %s, \n" +
                        "state %s \n" +
                        "monitoring state %s \n" +
                        "DNS %s \n\n",
                        search_name,
                        instance.getInstanceId(),
                        instance.getImageId(),
                        instance.getInstanceType(),
                        instance.getState().getName(),
                        instance.getMonitoring().getState(),
                		instance.getPublicDnsName());
            }
        }
        System.out.println("------------------------------------------------------------");

	}

	public static void getsshlink(String instance_name){
		String publicDNS = nameToDNS(instance_name);
        String user = "ec2-user";
        awsTest instance = new awsTest();
		String privateKey = instance.privateKey;

 		// 실행할 명령어
        String command = """
            ssh -i %s %s@%s
            """.formatted(privateKey, user, publicDNS);

        System.out.println(command);

	}

	public static void runProgram(String file_name, String instance_name){
		String exe_name = file_name.substring(0, file_name.lastIndexOf("."));
        String publicDNS = nameToDNS(instance_name);

	    Channel channel = null;
	    Session session = null;

	    System.out.println("connect to " + publicDNS + "\n\n");


        try{
            JSch jsch=new JSch();

            String user = "ec2-user";
            String host = publicDNS;
            int port = 22;
            awsTest instance = new awsTest();
    		String privateKey = instance.privateKey;

            jsch.addIdentity(privateKey);

            session = jsch.getSession(user, host, port);

            session.setConfig("StrictHostKeyChecking","no");
            session.setConfig("GSSAPIAuthentication","no");
            session.setServerAliveInterval(120 * 1000);
            session.setServerAliveCountMax(1000);
            session.setConfig("TCPKeepAlive","yes");

            session.connect();

            String responseString = "";
	        String[] commands = {"""
	        	printf "executable    = %s\nuniverse    = vanilla\noutput    = out.txt\nerror    = error.txt\nlog    = log.txt\nqueue" > %s.jds
	        	""".formatted(exe_name, exe_name), """
	        	sudo gcc -o %s %s
	        	""".formatted(exe_name, file_name), """
	        	condor_submit %s.jds
	        	""".formatted(exe_name)};

	        for (String command : commands) {
	            try {
					ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
					
					ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
					channelExec.setCommand(command);
					channelExec.setOutputStream(responseStream);
					channelExec.connect();
			        while (channelExec.isConnected()) {
			            Thread.sleep(100);
			        }
			        
			        responseString = new String(responseStream.toByteArray());
				} catch (JSchException e) {
					e.printStackTrace();
				}
	        }
			System.out.println("Complete!");
        }
        catch(Exception e){
            e.printStackTrace();
        } finally {
	        if (channel != null) {
	            channel.disconnect();
	        }
	        
	        if (session != null) {
	            session.disconnect();
	        }
		}
	}

	public static void sendFile(String file_name, String instance_name){
		String publicDNS = nameToDNS(instance_name);
        String user = "ec2-user";
        awsTest instance = new awsTest();
		String privateKey = instance.privateKey;

 		// 실행할 명령어
        String command = """
            scp -i %s %s %s@%s:/home/%s/%s
            """.formatted(privateKey, file_name, user, publicDNS, user, file_name);

        try {
            // ProcessBuilder를 사용하여 명령어 실행
            Process process = new ProcessBuilder(command.split("\\s+"))
                    .redirectErrorStream(true)
                    .start();

            // 명령어 실행 결과를 읽기 위한 BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // 명령어 실행 결과를 출력
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 프로세스가 종료될 때까지 대기
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}

    public static void sshConnect(){
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        //Create the Filter to use to find running instances
        Filter filter = new Filter("instance-state-name");
        filter.withValues("running");

        //Create a DescribeInstancesRequest
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withFilters(filter);

        // Find the running instances
        DescribeInstancesResult response = ec2.describeInstances(request);

        String publicDNS = null;

        for (Reservation reservation : response.getReservations()){

            for (Instance instance : reservation.getInstances()) {
                publicDNS = instance.getPublicDnsName();
            }
        }

	    Channel channel = null;
	    Session session = null;

	    if (publicDNS == null){
	    	System.out.println("Name                                             OpSys      Arch   State     Activity LoadAv Mem   ActvtyTime \n\n" +
               					"                  Machines Owner Claimed Unclaimed Matched Preempting  Drain\n" + 
								"  X86_64/LINUX        0     0       0         0       0          0       0\n" +
         						"  Total               0     0       0         0       0          0       0\n\n");
	    }
	    else{
		    System.out.println("connect to " + publicDNS + "\n\n");


	        try{
	            JSch jsch=new JSch();

	            String user = "ec2-user";
	            String host = publicDNS;
	            int port = 22;
	            awsTest instance = new awsTest();
        		String privateKey = instance.privateKey;

	            jsch.addIdentity(privateKey);

	            session = jsch.getSession(user, host, port);

	            session.setConfig("StrictHostKeyChecking","no");
	            session.setConfig("GSSAPIAuthentication","no");
	            session.setServerAliveInterval(120 * 1000);
	            session.setServerAliveCountMax(1000);
	            session.setConfig("TCPKeepAlive","yes");

	            session.connect();

	            String responseString = "";
	            String command = "condor_status"; // 실행할 명령어
				try {
					ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
					
					ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
					channelExec.setCommand(command);
					channelExec.setOutputStream(responseStream);
					channelExec.connect();
			        while (channelExec.isConnected()) {
			            Thread.sleep(100);
			        }
			        
			        responseString = new String(responseStream.toByteArray());
				} catch (JSchException e) {
					e.printStackTrace();
				}
				System.out.println(responseString);
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        } finally {
	        if (channel != null) {
	            channel.disconnect();
	        }
	        
	        if (session != null) {
	            session.disconnect();
	        }
	    }
      }
    }

	public static void listInstances() {
		
		System.out.println("Listing instances....");
		boolean done = false;
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					String instance_name = null;
					if (instance.getTags() != null) {
			            Tag tagName = instance.getTags().stream()
	                        .filter(o -> o.getKey().equals("Name"))
	                        .findFirst()
	                        .orElse(new Tag("Name", "name not found"));

		                instance_name = tagName.getValue();
			        }

					System.out.printf(
						"[name] %s, " +
						"[id] %s, " +
						"[AMI] %s, " +
						"[type] %s, " +
						"[state] %10s, " +
						"[monitoring state] %s",
						instance_name,
						instance.getInstanceId(),
						instance.getImageId(),
						instance.getInstanceType(),
						instance.getState().getName(),
						instance.getMonitoring().getState());
				}
				System.out.println();
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	public static void availableZones()	{

		System.out.println("Available zones....");
		try {
			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			Iterator <AvailabilityZone> iterator = availabilityZonesResult.getAvailabilityZones().iterator();
			
			AvailabilityZone zone;
			while(iterator.hasNext()) {
				zone = iterator.next();
				System.out.printf("[id] %s,  [region] %15s, [zone] %15s\n", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
			}
			System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
					" Availability Zones.");

		} catch (AmazonServiceException ase) {
				System.out.println("Caught Exception: " + ase.getMessage());
				System.out.println("Reponse Status Code: " + ase.getStatusCode());
				System.out.println("Error Code: " + ase.getErrorCode());
				System.out.println("Request ID: " + ase.getRequestId());
		}
	
	}

	public static void startInstance(String instance_id)
	{
		
		System.out.printf("Starting .... %s\n", instance_id);
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StartInstancesRequest> dry_request =
			() -> {
			StartInstancesRequest request = new StartInstancesRequest()
				.withInstanceIds(instance_id);

			return request.getDryRunRequest();
		};

		StartInstancesRequest request = new StartInstancesRequest()
			.withInstanceIds(instance_id);

		ec2.startInstances(request);

		System.out.printf("Successfully started instance %s\n", instance_id);
	}

	public static void stopInstance(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StopInstancesRequest> dry_request =
			() -> {
			StopInstancesRequest request = new StopInstancesRequest()
				.withInstanceIds(instance_id);

			return request.getDryRunRequest();
		};

		try {
			StopInstancesRequest request = new StopInstancesRequest()
				.withInstanceIds(instance_id);
	
			ec2.stopInstances(request);
			System.out.printf("Successfully stop instance %s\n", instance_id);

		} catch(Exception e)
		{
			System.out.println("Exception: "+e.toString());
		}

	}

	public static String nameToID(String instance_name){
		boolean done = false;
		String instance_id = null;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					String search_name = null;
					if (instance.getTags() != null) {
			            Tag tagName = instance.getTags().stream()
	                        .filter(o -> o.getKey().equals("Name"))
	                        .findFirst()
	                        .orElse(new Tag("Name", "name not found"));

		                search_name = tagName.getValue();
			        }

			        if (instance_name.equals(search_name)){
			        	instance_id = instance.getInstanceId();
			        	break;
			        }
				}
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
		return instance_id;
	}


	public static String nameToDNS(String instance_name){
		boolean done = false;
		String publicDNS = null;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					String search_name = null;
					if (instance.getTags() != null) {
			            Tag tagName = instance.getTags().stream()
	                        .filter(o -> o.getKey().equals("Name"))
	                        .findFirst()
	                        .orElse(new Tag("Name", "name not found"));

		                search_name = tagName.getValue();
			        }

			        if (instance_name.equals(search_name)){
			        	publicDNS = instance.getPublicDnsName();
			        	break;
			        }
				}
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
		return publicDNS;
	}

	public static void startInstance_name(String instance_name)
	{
		String instance_id = nameToID(instance_name);

		if (instance_id != null){
			startInstance(instance_id);
		}
	}

	public static void stopInstance_name(String instance_name)
	{
		String instance_id = nameToID(instance_name);

		if (instance_id != null){
			stopInstance(instance_id);
		}
	}
	
	
	public static void availableRegions() {
		
		System.out.println("Available regions ....");
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
			System.out.printf(
				"[region] %15s, " +
				"[endpoint] %s\n",
				region.getRegionName(),
				region.getEndpoint());
		}
	}
	
	public static void startAll(){
		AmazonEC2 ec2_all = AmazonEC2ClientBuilder.defaultClient();

        //Create the Filter to use to find running instances
        Filter filter = new Filter("instance-state-name");
        filter.withValues("stopped");

        //Create a DescribeInstancesRequest
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withFilters(filter);

        // Find the running instances
        DescribeInstancesResult response = ec2_all.describeInstances(request);

        for (Reservation reservation : response.getReservations()){

            for (Instance instance : reservation.getInstances()) {
            	String instance_id = instance.getInstanceId();
				startInstance(instance_id);
            }
        }
	}

	public static void stopAll(){
		AmazonEC2 ec2_all = AmazonEC2ClientBuilder.defaultClient();

        //Create the Filter to use to find running instances
        Filter filter = new Filter("instance-state-name");
        filter.withValues("running");

        //Create a DescribeInstancesRequest
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withFilters(filter);

        // Find the running instances
        DescribeInstancesResult response = ec2_all.describeInstances(request);

        for (Reservation reservation : response.getReservations()){

            for (Instance instance : reservation.getInstances()) {
            	String instance_id = instance.getInstanceId();
				stopInstance(instance_id);
            }
        }
	}
	
	public static void createInstance(String ami_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		
		RunInstancesRequest run_request = new RunInstancesRequest()
			.withImageId(ami_id)
			.withInstanceType(InstanceType.T2Micro)
			.withMaxCount(1)
			.withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		System.out.printf(
			"Successfully started EC2 instance %s based on AMI %s",
			reservation_id, ami_id);
	
	}

	public static void rebootInstance(String instance_id) {
		
		System.out.printf("Rebooting .... %s\n", instance_id);
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			RebootInstancesRequest request = new RebootInstancesRequest()
					.withInstanceIds(instance_id);

				RebootInstancesResult response = ec2.rebootInstances(request);

				System.out.printf(
						"Successfully rebooted instance %s", instance_id);

		} catch(Exception e)
		{
			System.out.println("Exception: "+e.toString());
		}

		
	}
	
	public static void listImages() {
		System.out.println("Listing images....");
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		
		DescribeImagesRequest request = new DescribeImagesRequest();
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		
		request.getFilters().add(new Filter().withName("name").withValues("Slave"));
		request.setRequestCredentialsProvider(credentialsProvider);
		
		DescribeImagesResult results = ec2.describeImages(request);
		
		for(Image images :results.getImages()){
			System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n", 
					images.getImageId(), images.getName(), images.getOwnerId());
		}
		
	}
}
	
