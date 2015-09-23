% coloanele citite reprezinta:
%RespTime, Availability, Throughput, Succesability, Reliability, -> 
% -> Compliance, BestPractices, Latency, Documentation

a = csvread('qosDBcoloaneSterse.csv');
% ++++++++++++++++ prelucrare date reale ++++++++++++
b = a;
b(:,1) = a(:,1)./1000000;
b(:,2) = a(:,2)./100;
b(:,4) = a(:,4)./100;
b(:,5) = a(:,5)./100;
b(:,8) = a(:,8)./1000000;
% csvwrite('dateRealePrelucrateSI.csv',b);
c = b;
c(:,1) = normc(b(:,1));
c(:,8) = normc(b(:,8));
csvwrite('dateRealePrelucrateSIsiNormate.csv',c);

% b = normc(a);
% csvwrite('dateRealeQoSNormate.csv',b);
% csvwrite('dateRealeQoS.csv',a);


%++++++++++ histograme cu datele reale +++++++++++
% figure(1);
% subplot(2,1,1);
% hist(a(:,1)');
% title('Response time');
% subplot(2,1,2);
% hist(normr(a(:,1)'));
% title('Response time normalizat');
% 
% figure(2);
% subplot(2,1,1);
% hist(a(:,2)');
% title('Availability');
% subplot(2,1,2);
% hist(normr(a(:,2)'));
% title('Availability normalizat');
% 
% figure(3);
% subplot(2,1,1);
% hist(a(:,3)');
% title('Throughput');
% subplot(2,1,2);
% hist(normr(a(:,3)'));
% title('Throughput normalizat');
% 
% figure(4);
% subplot(2,1,1);
% hist(a(:,4)');
% title('Succesability');
% subplot(2,1,2);
% hist(normr(a(:,4)'));
% title('Succesability normalizat');
% 
% figure(5);
% subplot(2,1,1);
% hist(a(:,5)');
% title('Reliability');
% subplot(2,1,2);
% hist(normr(a(:,5)'));
% title('Reliability normalizat');
% 
% figure(6);
% subplot(2,1,1);
% hist(a(:,6)');
% title('Compliance');
% subplot(2,1,2);
% hist(normr(a(:,6)'));
% title('Compliance normalizat');
% 
% figure(7);
% subplot(2,1,1);
% hist(a(:,7)');
% title('BestPractices');
% subplot(2,1,2);
% hist(normr(a(:,7)'));
% title('BestPractices normalizat');
% 
% figure(8);
% subplot(2,1,1);
% hist(a(:,8)');
% title('Latency');
% subplot(2,1,2);
% hist(normr(a(:,8)'));
% title('Latency normalizat');