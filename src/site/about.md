# Paystream Generator

The paystream generator creates paystreams for one of three built-in instance classes represented by Class 1 with 30 jobs, Class 2 with 60 jobs or Class 3 with 120 jobs. The number of agents respectively paystream files can be chosen dynamically but due to uniform random distribution of jobs per agent the number of agents must be dividable by the number of jobs without remainder. Currently two algorithms are available to determine the payment values together with job-to-agent assignments:

## 1.	RandomPaymentComputation

Random value selection from a given min-max value range per job without further logic.

## 2.	AscendingPaymentComputation

Negative value selection for the first half part of the instance jobs in a specified negative min-max value range and positive value selection for the second half part with a positive value range inherited from the negative value range.  Additionally a global probability distribution for each job is used as an inflow factor (50%) for the value selection besides randomness (50%). The computation of the probability map distribution is done by summing up the occurrence per job in all possible paths between start and end job for all available instances for the given class (12 instances per class). 

The computed paystream values are saved and updated internally inside a payment matrix with one column per agent and row for each job. Afterwards a validation process checks the payment value sum for each agent by its assigned jobs. This sum must be non-negative, otherwise the created paystream dataset will be discarded. Lastly the generated matrix is written out to a separate file per agent.
