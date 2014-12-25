/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.kata;

import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class Exercise9Test extends CompanyDomainForKata
{
    /**
     * Extra credit. Aggregate the total order values by city.  Hint: Look at RichIterable.aggregateBy.
     */
    @Test
    public void totalOrderValuesByCity()
    {
        Function0<Double> zeroValueFactory = () -> Double.valueOf(0.0);

        Function2<Double, Customer, Double> aggregator = 
        		(Double result, Customer customer) -> result + customer.getTotalOrderValue(); 
      
        MutableMap<String, Double> map = this.company.getCustomers()
        		.aggregateBy(Customer.TO_CITY, zeroValueFactory, aggregator);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(446.25, map.get("London"), 0.0);
        Assert.assertEquals(857.0, map.get("Liphook"), 0.0);
    }

    /**
     * Extra credit. Aggregate the total order values by item.  Hint: Look at RichIterable.aggregateBy and remember
     * how to use flatCollect to get an iterable of all items.
     */
    @Test
    public void totalOrderValuesByItem()
    {
    	 Function0<Double> zeroValueFactory = () -> Double.valueOf(0.0);

         Function2<Double, LineItem, Double> aggregator = 
         		(Double result, LineItem lineItem) -> result + lineItem.getValue(); 
         		

        MutableMap<String, Double> map = this.company.getOrders().flatCollect(Order.TO_LINE_ITEMS)
        		.aggregateBy(LineItem.TO_NAME, zeroValueFactory, aggregator);
        Verify.assertSize(12, map);
        Assert.assertEquals(100.0, map.get("shed"), 0.0);
        Assert.assertEquals(10.5, map.get("cup"), 0.0);
    }

    /**
     * Extra credit. Find all customers' line item values greater than 7.5 and sort them by highest to lowest price.
     */
    @Test
    public void sortedOrders()
    {
        MutableSortedBag<Double> orderedPrices = 
        		TreeBag.newBag(Collections.reverseOrder(), this.company.getOrders()
        		.flatCollect(Order.TO_LINE_ITEMS)
        		.select(item -> item.getValue() > 7.5)
        		.collect(LineItem::getValue));

        MutableSortedBag<Double> expectedPrices = TreeBag.newBagWith(
                Collections.reverseOrder(), 500.0, 150.0, 120.0, 75.0, 50.0, 50.0, 12.5);
        Verify.assertSortedBagsEqual(expectedPrices, orderedPrices);
    }

    /**
     * Extra credit. Figure out which customers ordered saucers (in any of their orders).
     */
    @Test
    public void whoOrderedSaucers()
    {
    	Predicate<Customer> ORDERED_SAUCER = new Predicate<Customer>() {
    		@Override
    		public boolean accept(Customer customer) {
    			MutableList<LineItem> items = customer.getOrders().flatCollect(Order.TO_LINE_ITEMS);
    			return items.anySatisfy(Predicates.attributeEqual(LineItem.TO_NAME, "saucer"));
    		}
    	};
    	
        MutableList<Customer> customersWithSaucers = this.company.getCustomers()
        		.select(ORDERED_SAUCER);
        Verify.assertSize("customers with saucers", 2, customersWithSaucers);
    }

    /**
     * Extra credit. Look into the {@link MutableList#toMap(Function, Function)} method.
     */
    @Test
    public void ordersByCustomerUsingAsMap()
    {
        MutableMap<String, MutableList<Order>> customerNameToOrders =
                this.company.getCustomers().toMap(Customer.TO_NAME, Customer::getOrders);

        Assert.assertNotNull("customer name to orders", customerNameToOrders);
        Verify.assertSize("customer names", 3, customerNameToOrders);
        MutableList<Order> ordersForBill = customerNameToOrders.get("Bill");
        Verify.assertSize("Bill orders", 3, ordersForBill);
    }

    /**
     * Extra credit. Create a multimap where the values are customers and the key is the price of
     * the most expensive item that the customer ordered.
     */
    @Test
    public void mostExpensiveItem()
    {
    	Function<Customer, Double> MAX_PRICE = new Function<Customer, Double>() {
    		
    		@Override
            public Double valueOf(Customer customer)
            {
                return customer.getOrders().flatCollect(Order.TO_LINE_ITEMS).collect(LineItem::getValue).max();
            }
    		
    	};
    	
        MutableListMultimap<Double, Customer> multimap = 
        		this.company.getCustomers().groupBy(MAX_PRICE);
        Assert.assertEquals(3, multimap.size());
        Assert.assertEquals(2, multimap.keysView().size());
        Assert.assertEquals(
                FastList.newListWith(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                multimap.get(50.0));
    }
}
