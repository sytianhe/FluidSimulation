function [] = ProcessData( fileName, skipLines )
% Process data in file

data = zeros(40,10,6);
maxDisp = zeros(1,400);
wingLoad = zeros(1,400);
excentricity = zeros(1,400);
FinalDispX = zeros(1,400);
majorAxis = zeros(1,400);
minorAxis = zeros(1,400);


fid = fopen(fileName);

currentLine = 1;
while ~feof(fid)
  %skip lines at beginning
  if(currentLine<=skipLines)
    fgetl(fid);
  else
    s = fgetl(fid);
    r = regexp(s, ' ', 'split');
    r =str2double(r);
    i = int8(r(1)) + 1;
    j = int8(r(2)) + 1;
    data(i,j,:)=r(3:end);
    
    %generate other data values
    majorAxis(currentLine-skipLines) = r(3);
    minorAxis(currentLine-skipLines) = r(4);
    maxDisp(currentLine-skipLines) = r(7);
    FinalDispX(currentLine-skipLines) = r(8);
    wingLoad(currentLine-skipLines) = pi * r(4);
    excentricity(currentLine-skipLines) = r(4) / r(3);
    
  end
  currentLine = currentLine + 1;

end
fclose(fid);

%Do something w
% figure(1);
% 
% hist(data(:,:,5));
% 
% figure(2);
% 
% hist(maxDisp);
% 
% figure(3);
% 
% scatter(wingLoad, maxDisp);
% 
% 
% figure(4);
% 
% scatter(excentricity, maxDisp);
% 
% figure(5);
% 
% hist(maxDisp);

% excentricity, 
% wingload vs. velocity
% 3D plot (major vs minor vs v

%figure

%plot3(majorAxis, minorAxis, maxDisp, 'o');

ecen  = zeros(40,1);
for i = 1:1:40,
    ecen(i) = excentricity(i*10);
end
[p, index] = sort(ecen);

averageMaxDisp = zeros(40,1);
for i = 1:1:40,
    temp = 0;
    for j = 1:1:10,
        data(i,j,1)
        temp = temp + data(i,j,3);
    end
    averageMaxDisp(i) = temp/10;
end

figure(1)
plot(p, averageMaxDisp(index), 'o-')

% wingLoad  = zeros(40,1);
% for i = 1:1:40,
%     wingLoad(i) = 1/majorAxis(i*10)/minorAxis(i*10);
% end
% [p, index] = sort(wingLoad);
% 
% figure(2)
% plot(p, averageMaxDisp(index), 'o-')

rowtemp=[];
for i = 1:1:40,
    temp = [];
    for j = 1:1:10,
        temp = [temp, data(i,j,3)];
    end
    rowtemp = [rowtemp;temp];
end

sortedRow = zeros(40,10);
for i = 1:1:40
    sortedRow(i,:) = rowtemp(index(i),:)
end

figure(2)
boxplot(sortedRow','position',p)

subplot(2,1,1)
plot(p, averageMaxDisp(index), 'ro-')

subplot(2,1,2)
boxplot(sortedRow','plotstyle','compact','position',p)

